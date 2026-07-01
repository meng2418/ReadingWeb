package com.weread.service.impl.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weread.dto.reader.AiInterpretRequestDTO;
import com.weread.service.reader.AiInterpretService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiInterpretServiceImpl implements AiInterpretService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long SSE_TIMEOUT_MS = 300_000L;
    private static final String OLLAMA_GENERATE = "/api/generate";

    private final RestTemplate restTemplate;

    @Value("${app.ai.llm-url:}")
    private String llmUrl;

    @Value("${app.ai.base-url:}")
    private String baseUrl;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:qwen2.5:latest}")
    private String model;

    @Value("${app.ai.chat.completions-path:/v1/chat/completions}")
    private String chatCompletionsPath;

    @Override
    public String interpret(AiInterpretRequestDTO request) {
        if (llmUrl != null && !llmUrl.isBlank()) {
            return interpretViaOllama(request);
        }
        if (baseUrl != null && !baseUrl.isBlank() && apiKey != null && !apiKey.isBlank()) {
            return interpretViaCloud(request);
        }
        throw new IllegalStateException("AI not configured: set app.ai.llm-url or app.ai.base-url + app.ai.api-key");
    }

    @Override
    public SseEmitter streamInterpret(AiInterpretRequestDTO request) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        new Thread(() -> {
            try {
                String fullText;
                if (llmUrl != null && !llmUrl.isBlank()) {
                    fullText = streamViaOllama(request, emitter);
                } else if (baseUrl != null && !baseUrl.isBlank() && apiKey != null && !apiKey.isBlank()) {
                    fullText = streamViaCloud(request, emitter);
                } else {
                    sendSseError(emitter, "AI not configured");
                    return;
                }
                sendSseDone(emitter, fullText.isBlank() ? "(empty response)" : fullText);
            } catch (Exception e) {
                log.warn("Stream interpret failed: {}", e.getMessage());
                sendSseError(emitter, "AI interpret failed: " + e.getMessage());
            }
        }).start();

        emitter.onTimeout(() -> sendSseError(emitter, "Request timeout"));
        emitter.onError(ex -> log.warn("SSE error: {}", ex.getMessage()));
        return emitter;
    }

    private String interpretViaOllama(AiInterpretRequestDTO request) {
        String prompt = buildPrompt(request);
        String url = llmUrl.replaceAll("/$", "") + OLLAMA_GENERATE;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of("model", model, "prompt", prompt, "stream", false);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                    url, entity, (Class<Map<String, Object>>) (Class<?>) Map.class);
            Map<String, Object> bodyResp = response.getBody();
            if (bodyResp != null && bodyResp.containsKey("response")) {
                Object text = bodyResp.get("response");
                return text != null ? text.toString().trim() : "(empty response)";
            }
            throw new IllegalStateException("Unexpected Ollama response");
        } catch (Exception e) {
            log.warn("Ollama call failed: {}", e.getMessage());
            throw new IllegalStateException("Ollama call failed: " + e.getMessage());
        }
    }

    private String interpretViaCloud(AiInterpretRequestDTO request) {
        String prompt = buildPrompt(request);
        String url = baseUrl.replaceAll("/$", "") + chatCompletionsPath;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "stream", false
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                    url, entity, (Class<Map<String, Object>>) (Class<?>) Map.class);
            Map<String, Object> bodyResp = response.getBody();
            if (bodyResp == null) {
                throw new IllegalStateException("empty response");
            }
            Object choicesObj = bodyResp.get("choices");
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
            throw new IllegalStateException("Unexpected cloud LLM response");
        } catch (Exception e) {
            log.warn("Cloud LLM call failed: {}", e.getMessage());
            throw new IllegalStateException("Cloud LLM call failed: " + e.getMessage());
        }
    }

    private String streamViaOllama(AiInterpretRequestDTO request, SseEmitter emitter) throws Exception {
        String prompt = buildPrompt(request);
        String url = llmUrl.replaceAll("/$", "") + OLLAMA_GENERATE;
        Map<String, Object> body = Map.of("model", model, "prompt", prompt, "stream", true);
        HttpURLConnection conn = openJsonPost(url, body, null);
        StringBuilder full = new StringBuilder();
        try (InputStream in = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                JsonNode node = MAPPER.readTree(line);
                if (node.has("response") && !node.get("response").isNull()) {
                    String chunk = node.get("response").asText();
                    if (!chunk.isEmpty()) {
                        full.append(chunk);
                        emitter.send(SseEmitter.event().name("chunk").data(toJson(chunk)));
                    }
                }
                if (node.path("done").asBoolean(false)) break;
            }
        } finally {
            conn.disconnect();
        }
        return full.toString().trim();
    }

    private String streamViaCloud(AiInterpretRequestDTO request, SseEmitter emitter) throws Exception {
        String prompt = buildPrompt(request);
        String url = baseUrl.replaceAll("/$", "") + chatCompletionsPath;
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        body.put("temperature", 0.7);
        body.put("stream", true);
        HttpURLConnection conn = openJsonPost(url, body, apiKey);
        return streamOpenAiFromConn(conn, emitter);
    }

    private String streamOpenAiFromConn(HttpURLConnection conn, SseEmitter emitter) throws Exception {
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
                        emitter.send(SseEmitter.event().name("chunk").data(toJson(chunk)));
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
        conn.setRequestProperty("Accept", "text/event-stream, application/x-ndjson, application/json");
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

    private void sendSseDone(SseEmitter emitter, String fullText) {
        try {
            emitter.send(SseEmitter.event().name("done").data(toJson(fullText)));
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    /** 将文本编码为 JSON 字符串字面量，确保 SSE data 单行无换行，避免分块/分帧错乱。 */
    private String toJson(String value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            return "\"" + value
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r") + "\"";
        }
    }

    private void sendSseError(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().name("error").data(message));
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private String buildPrompt(AiInterpretRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("请对以下书中选段做简要解读（2～5 句话），语言简洁、易懂。\n\n");
        if (request.getBookTitle() != null && !request.getBookTitle().isBlank()) {
            sb.append("书名：《").append(request.getBookTitle()).append("》\n");
        }
        if (request.getChapterTitle() != null && !request.getChapterTitle().isBlank()) {
            sb.append("章节：").append(request.getChapterTitle()).append("\n");
        }
        sb.append("选段：\n「").append(request.getSelectedText()).append("」\n\n");
        if (request.getFollowUp() != null && !request.getFollowUp().isBlank()) {
            sb.append("读者追问：").append(request.getFollowUp()).append("\n\n请针对追问回答。\n");
        }
        sb.append("解读：");
        return sb.toString();
    }
}
