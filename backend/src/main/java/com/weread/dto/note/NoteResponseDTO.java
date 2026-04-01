package com.weread.dto.note;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 笔记响应DTO
 */
@Data
public class NoteResponseDTO {
    private Integer noteId;
    private String quote;
    private String lineType;
    private String noteContent; // 对应thought
    private LocalDateTime createdAt;
    private Integer rangeStart; // 笔记在当前章节原文中的起始字符索引
    private Integer rangeEnd; // 笔记在当前章节原文中的结束字符索引
}
