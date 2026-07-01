package com.weread.service.impl.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weread.entity.book.BookEntity;
import com.weread.entity.reader.AiChatMessageEntity;
import com.weread.repository.book.BookRepository;
import com.weread.repository.reader.AiChatMessageRepository;
import com.weread.service.reader.AiChatService;
import com.weread.vo.reader.AiChatHistoryVO;
import com.weread.vo.reader.AiChatMessageVO;
import com.weread.vo.reader.AiChatSendMessageResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final AiChatMessageRepository messageRepository;
    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;

    @Value("${app.ai.provider:openai-compatible}")
    private String provider;

    @Value("${app.ai.base-url:}")
    private String baseUrl;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:qwen-plus}")
    private String model;

    /** 未配置有效 API Key 时是否返回本地模拟回复（本地/契约测试） */
    @Value("${app.ai.mock-enabled:true}")
    private boolean mockEnabled;

    @Value("${app.ai.chat.system-prompt:You are a reading assistant. Answer questions about the current book. If uncertain, say so and provide a reasonable guess.}")
    private String systemPrompt;

    @Value("${app.ai.chat.context-limit:20}")
    private int contextLimit;

    @Value("${app.ai.chat.completions-path:/v1/chat/completions}")
    private String chatCompletionsPath;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final long SSE_TIMEOUT_MS = 300_000L;

    @Override
    public AiChatHistoryVO getHistory(Integer userId, Integer bookId, Integer limit, Integer cursor) {
        int pageSize = (limit == null || limit <= 0) ? 50 : Math.min(limit, 200);

        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new IllegalArgumentException("book not found"));

        PageRequest pageable = PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.DESC, "messageId"));
        Page<AiChatMessageEntity> page;
        if (cursor == null) {
            page = messageRepository.findByUserIdAndBookId(userId, bookId, pageable);
        } else {
            page = messageRepository.findByUserIdAndBookIdAndMessageIdLessThan(userId, bookId, cursor, pageable);
        }

        List<AiChatMessageEntity> list = page.getContent();
        boolean hasMore = list.size() > pageSize;
        if (hasMore) {
            list = list.subList(0, pageSize);
        }

        Integer nextCursor = hasMore && !list.isEmpty()
                ? list.get(list.size() - 1).getMessageId()
                : null;

        List<AiChatMessageVO> messages = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            messages.add(toVO(list.get(i)));
        }

        return AiChatHistoryVO.builder()
                .bookTitle(book.getTitle())
                .messages(messages)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }

    @Override
    public AiChatSendMessageResponseVO sendMessage(Integer userId, Integer bookId, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new IllegalArgumentException("book not found"));

        AiChatMessageEntity userMsg = new AiChatMessageEntity();
        userMsg.setUserId(userId);
        userMsg.setBookId(bookId);
        userMsg.setRole("user");
        userMsg.setContent(message.trim());
        userMsg = messageRepository.save(userMsg);

        String assistantText = callCloudChat(userId, book, message.trim());

        AiChatMessageEntity assistantMsg = new AiChatMessageEntity();
        assistantMsg.setUserId(userId);
        assistantMsg.setBookId(bookId);
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(assistantText);
        assistantMsg = messageRepository.save(assistantMsg);

        return AiChatSendMessageResponseVO.builder()
                .userMessage(toVO(userMsg))
                .assistantMessage(toVO(assistantMsg))
                .build();
    }

    @Override
    public SseEmitter streamMessage(Integer userId, Integer bookId, String message) {
        if (message == null || message.isBlank()) {
            SseEmitter emitter = new SseEmitter(0L);
            sendSseError(emitter, "message is required");
            return emitter;
        }

        BookEntity book;
        try {
            book = bookRepository.findByBookId(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("book not found"));
        } catch (IllegalArgumentException e) {
            SseEmitter emitter = new SseEmitter(0L);
            sendSseError(emitter, e.getMessage());
            return emitter;
        }

        AiChatMessageEntity userMsg = new AiChatMessageEntity();
        userMsg.setUserId(userId);
        userMsg.setBookId(bookId);
        userMsg.setRole("user");
        userMsg.setContent(message.trim());
        userMsg = messageRepository.save(userMsg);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        final AiChatMessageEntity savedUserMsg = userMsg;
        final BookEntity resolvedBook = book;
        final String trimmedMessage = message.trim();

        new Thread(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("meta")
                        .data("{\"userMessageId\":" + savedUserMsg.getMessageId() + "}"));

                String fullText;
                if (shouldUseMock()) {
                    log.info("AI mock mode enabled (no API key configured); streaming mock reply");
                    fullText = streamMockReply(resolvedBook, trimmedMessage, emitter);
                } else {
                    List<Map<String, String>> chatMessages = buildChatMessages(userId, resolvedBook, trimmedMessage);
                    validateCloudConfig();
                    String url = baseUrl.replaceAll("/$", "") + chatCompletionsPath;
                    fullText = streamOpenAiChat(url, chatMessages, emitter);
                }

                AiChatMessageEntity assistantMsg = new AiChatMessageEntity();
                assistantMsg.setUserId(userId);
                assistantMsg.setBookId(bookId);
                assistantMsg.setRole("assistant");
                assistantMsg.setContent(fullText.isBlank() ? "(empty response)" : fullText);
                assistantMsg = messageRepository.save(assistantMsg);

                emitter.send(SseEmitter.event()
                        .name("done")
                        .data("{\"messageId\":" + assistantMsg.getMessageId()
                                + ",\"content\":" + jsonString(assistantMsg.getContent()) + "}"));
                emitter.complete();
            } catch (Exception e) {
                log.error("Stream AI chat failed: {}", e.getMessage(), e);
                sendSseError(emitter, "Cloud LLM call failed: " + e.getMessage());
            }
        }).start();

        emitter.onTimeout(() -> sendSseError(emitter, "Request timeout"));
        return emitter;
    }

    private String streamOpenAiChat(String url, List<Map<String, String>> chatMessages, SseEmitter emitter)
            throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", chatMessages);
        body.put("temperature", 0.7);
        body.put("stream", true);

        HttpURLConnection conn = openJsonPost(url, body, apiKey);
        StringBuilder full = new StringBuilder();
        try (InputStream in = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || !line.startsWith("data:")) continue;
                String payload = line.substring(5).trim();
                if ("[DONE]".equals(payload)) break;
                JsonNode node = MAPPER.readTree(payload);
                JsonNode delta = node.path("choices").path(0).path("delta");
                if (delta.has("content") && !delta.get("content").isNull()) {
                    String chunk = delta.get("content").asText();
                    if (!chunk.isEmpty()) {
                        full.append(chunk);
                        emitter.send(SseEmitter.event().name("chunk").data(jsonString(chunk)));
                    }
                }
            }
        } finally {
            conn.disconnect();
        }
        return full.toString().trim();
    }

    private HttpURLConnection openJsonPost(String url, Map<String, Object> body, String bearerToken) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout((int) SSE_TIMEOUT_MS);
        conn.setReadTimeout((int) SSE_TIMEOUT_MS);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "text/event-stream, application/json");
        if (bearerToken != null && !bearerToken.isBlank()) {
            conn.setRequestProperty("Authorization", "Bearer " + bearerToken);
        }
        byte[] payload = MAPPER.writeValueAsBytes(body);
        conn.setFixedLengthStreamingMode(payload.length);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(payload);
        }
        if (conn.getResponseCode() >= 400) {
            throw new IllegalStateException("LLM HTTP " + conn.getResponseCode());
        }
        return conn;
    }

    private void sendSseError(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().name("error").data(message));
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private String jsonString(String value) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(value);
        } catch (Exception e) {
            return "\"" + value.replace("\"", "\\\"") + "\"";
        }
    }

    private void validateCloudConfig() {
        if (!"openai-compatible".equalsIgnoreCase(provider)) {
            throw new IllegalStateException("Unsupported ai provider: " + provider);
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("LLM base url is not configured: set app.ai.base-url");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("LLM api key is not configured: set app.ai.api-key");
        }
    }

    /**
     * 当开启 mock 且云端配置不完整（缺少 api-key 或 base-url）时，使用本地模拟回复，
     * 以便在未接入真实大模型的情况下进行前后端联调与契约测试。
     */
    private boolean shouldUseMock() {
        if (!mockEnabled) {
            return false;
        }
        return apiKey == null || apiKey.isBlank()
                || baseUrl == null || baseUrl.isBlank();
    }

    /** 生成单行（无换行）的模拟回复，避免影响 SSE 的分块显示。 */
    private String buildMockReply(BookEntity book, String userMessage) {
        String title = (book != null && book.getTitle() != null && !book.getTitle().isBlank())
                ? book.getTitle() : "这本书";
        return "【本地模拟回复】关于《" + title + "》，你的问题是「" + userMessage + "」。"
                + "当前后端尚未配置有效的 AI API Key（app.ai.api-key 为空），因此返回本地模拟内容，用于前后端联调与契约测试。"
                + "配置真实的 API Key（或设置环境变量 APP_AI_API_KEY）后即可获得基于大模型的真实回答。";
    }

    /** 将模拟回复按小块通过 SSE 逐块推送，模拟流式输出效果，返回完整文本。 */
    private String streamMockReply(BookEntity book, String userMessage, SseEmitter emitter) throws Exception {
        String text = buildMockReply(book, userMessage);
        int step = 12;
        for (int i = 0; i < text.length(); i += step) {
            String chunk = text.substring(i, Math.min(text.length(), i + step));
            emitter.send(SseEmitter.event().name("chunk").data(jsonString(chunk)));
            try {
                Thread.sleep(30L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return text;
    }

    private List<Map<String, String>> buildChatMessages(Integer userId, BookEntity book, String latestUserMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt + "\nCurrent book: " + book.getTitle()));

        PageRequest pageable = PageRequest.of(0, Math.max(contextLimit, 1), Sort.by(Sort.Direction.DESC, "messageId"));
        List<AiChatMessageEntity> recent = new ArrayList<>(
                messageRepository.findByUserIdAndBookId(userId, book.getBookId(), pageable).getContent()
        );
        Collections.reverse(recent);
        for (AiChatMessageEntity m : recent) {
            if ("user".equals(m.getRole()) || "assistant".equals(m.getRole())) {
                messages.add(Map.of("role", m.getRole(), "content", m.getContent()));
            }
        }

        if (messages.isEmpty()
                || !"user".equals(messages.get(messages.size() - 1).get("role"))
                || !Objects.equals(messages.get(messages.size() - 1).get("content"), latestUserMessage)) {
            messages.add(Map.of("role", "user", "content", latestUserMessage));
        }
        return messages;
    }

    private AiChatMessageVO toVO(AiChatMessageEntity e) {
        return AiChatMessageVO.builder()
                .messageId(e.getMessageId())
                .role(e.getRole())
                .content(e.getContent())
                .createdAt(e.getCreatedAt() == null ? null : TIME_FMT.format(e.getCreatedAt()))
                .build();
    }

    private String callCloudChat(Integer userId, BookEntity book, String latestUserMessage) {
        if (shouldUseMock()) {
            log.info("AI mock mode enabled (no API key configured); returning mock reply");
            return buildMockReply(book, latestUserMessage);
        }
        validateCloudConfig();

        String url = baseUrl.replaceAll("/$", "") + chatCompletionsPath;
        log.info("Calling AI provider={}, model={}, url={}", provider, model, url);

        List<Map<String, String>> messages = buildChatMessages(userId, book, latestUserMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> resp = restTemplate.postForEntity(
                    url, entity, (Class<Map<String, Object>>) (Class<?>) Map.class);
            Map<String, Object> respBody = resp.getBody();
            if (respBody == null) {
                throw new IllegalStateException("empty response");
            }

            Object choicesObj = respBody.get("choices");
            if (choicesObj instanceof List<?> choices && !choices.isEmpty()) {
                Object first = choices.get(0);
                if (first instanceof Map<?, ?> firstChoice) {
                    Object msgObj = firstChoice.get("message");
                    if (msgObj instanceof Map<?, ?> msgMap) {
                        Object content = msgMap.get("content");
                        if (content != null) {
                            return content.toString().trim();
                        }
                    }
                }
            }

            throw new IllegalStateException("unexpected cloud LLM response format");
        } catch (HttpStatusCodeException e) {
            log.error("Cloud LLM HTTP call failed. status={}, response={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalStateException("Cloud LLM call failed: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Cloud LLM call failed. message={}", e.getMessage(), e);
            String message = e.getMessage();
            if (message == null || message.isBlank()) {
                message = e.getClass().getSimpleName();
            }
            throw new IllegalStateException("Cloud LLM call failed: " + message);
        }
    }
}