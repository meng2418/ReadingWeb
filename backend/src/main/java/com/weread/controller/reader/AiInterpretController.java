package com.weread.controller.reader;

import com.weread.dto.Result;
import com.weread.dto.reader.AiInterpretRequestDTO;
import com.weread.service.reader.AiInterpretService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 阅读器端侧大模型 AI 解读
 * 将选中文本转发到配置的本地 LLM（如 Ollama），返回解读结果
 */
@RestController
@RequestMapping("/reader/ai")
@RequiredArgsConstructor
@Tag(name = "阅读器AI", description = "端侧大模型解读选段")
@SecurityRequirement(name = "bearerAuth")
public class AiInterpretController {

    private final AiInterpretService aiInterpretService;

    @PostMapping("/interpret")
    @Operation(summary = "解读选中文本", description = "调用端侧大模型对书中选段做简要解读")
    public ResponseEntity<Result<Map<String, String>>> interpret(@RequestBody AiInterpretRequestDTO dto) {
        if (dto.getSelectedText() == null || dto.getSelectedText().isBlank()) {
            return ResponseEntity.badRequest().body(Result.fail("选段内容不能为空"));
        }
        String text = aiInterpretService.interpret(dto);
        return ResponseEntity.ok(Result.success(Map.of("text", text)));
    }
}
