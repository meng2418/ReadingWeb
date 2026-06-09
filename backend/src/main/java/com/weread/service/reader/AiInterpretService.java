package com.weread.service.reader;

import com.weread.dto.reader.AiInterpretRequestDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 端侧大模型解读：对书中选段生成解读文案
 */
public interface AiInterpretService {

    /**
     * 根据选中文本（及可选追问）调用端侧大模型，返回解读结果
     */
    String interpret(AiInterpretRequestDTO request);

    /**
     * 流式解读：通过 SSE 逐块返回模型输出
     */
    SseEmitter streamInterpret(AiInterpretRequestDTO request);
}
