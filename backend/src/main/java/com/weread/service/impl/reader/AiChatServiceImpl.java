package com.weread.service.impl.reader;

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

import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

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

    @Value("${app.ai.chat.system-prompt:You are a reading assistant. Answer questions about the current book. If uncertain, say so and provide a reasonable guess.}")
    private String systemPrompt;

    @Value("${app.ai.chat.context-limit:20}")
    private int contextLimit;

    @Value("${app.ai.chat.completions-path:/v1/chat/completions}")
    private String chatCompletionsPath;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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

        Integer nextCursor = hasMore && !list.isEmpty() ? list.get(list.size() - 1).getMessageId() : null;

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

    private AiChatMessageVO toVO(AiChatMessageEntity e) {
        return AiChatMessageVO.builder()
                .messageId(e.getMessageId())
                .role(e.getRole())
                .content(e.getContent())
                .createdAt(e.getCreatedAt() == null ? null : TIME_FMT.format(e.getCreatedAt()))
                .build();
    }

    private String callCloudChat(Integer userId, BookEntity book, String latestUserMessage) {
        if (!"openai-compatible".equalsIgnoreCase(provider)) {
            throw new IllegalStateException("Unsupported ai provider: " + provider);
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("LLM base url is not configured: set app.ai.base-url");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("LLM api key is not configured: set app.ai.api-key");
        }
        String effectiveApiKey = apiKey;

        String url = baseUrl.replaceAll("/$", "") + chatCompletionsPath;
        log.info("Calling AI provider={}, model={}, url={}", provider, model, url);

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(effectiveApiKey);

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