package com.weread.service.reader;

import com.weread.dto.reader.AiInterpretRequestDTO;

/**
 * 端侧大模型解读：对书中选段生成解读文案
 */
public interface AiInterpretService {

    /**
     * 根据选中文本（及可选追问）调用端侧大模型，返回解读结果
     */
    String interpret(AiInterpretRequestDTO request);
}
