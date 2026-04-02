package com.weread.dto.reader;

import lombok.Data;

/**
 * 阅读器 AI 解读请求（端侧大模型）
 */
@Data
public class AiInterpretRequestDTO {

    /** 用户选中的书中文本 */
    private String selectedText;

    /** 书名（可选，用于上下文） */
    private String bookTitle;

    /** 章节名（可选） */
    private String chapterTitle;

    /** 追问内容（可选） */
    private String followUp;
}
