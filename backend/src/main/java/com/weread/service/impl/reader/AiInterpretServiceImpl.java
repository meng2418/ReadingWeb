package com.weread.service.impl.reader;

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

import java.util.Map;

/**
 * 调用端侧大模型（Ollama 兼容接口）对选段做解读
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiInterpretServiceImpl implements AiInterpretService {

    private final RestTemplate restTemplate;

    @Value("${app.ai.llm-url:}")
    private String llmUrl;

    @Value("${app.ai.model:qwen2.5:latest}")
    private String model;

    private static final String OLLAMA_GENERATE = "/api/generate";

    @Override
    public String interpret(AiInterpretRequestDTO request) {
        if (llmUrl == null || llmUrl.isBlank()) {
            throw new IllegalStateException("端侧大模型未配置，请在 application.yml 中配置 app.ai.llm-url（如 http://127.0.0.1:11434）");
        }
        String prompt = buildPrompt(request);
        String baseUrl = llmUrl.replaceAll("/$", "");
        String url = baseUrl + OLLAMA_GENERATE;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                    url, entity, (Class<Map<String, Object>>) (Class<?>) Map.class);
            Map<String, Object> bodyResp = response.getBody();
            if (bodyResp != null && bodyResp.containsKey("response")) {
                Object text = bodyResp.get("response");
                return text != null ? text.toString().trim() : "（未生成内容）";
            }
            throw new IllegalStateException("端侧大模型返回格式异常");
        } catch (Exception e) {
            log.warn("调用端侧大模型失败: {}", e.getMessage());
            throw new IllegalStateException("端侧大模型调用失败，请确认本地已启动 Ollama 且模型可用: " + e.getMessage());
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
