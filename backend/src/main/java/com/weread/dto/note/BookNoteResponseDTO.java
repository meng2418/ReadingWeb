package com.weread.dto.note;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 全书笔记响应DTO
 * 对应接口：GET /reader/{bookId}/notes
 */
@Data
public class BookNoteResponseDTO {
    private Long noteId;
    private Integer bookId;
    private String bookTitle;
    private Integer chapterId;
    private String chapterName;
    private String noteType; // "line" 或 "thought"
    private String quote; // 原文引用内容
    private Integer startIndex; // 笔记起始位置（字符索引）
    private Integer endIndex; // 笔记结束位置（字符索引）
    private Integer pageNumber; // 笔记页码
    private String lineType; // 划线类型（marker、wavy、underline）
    private String thought; // 想法
    private LocalDateTime noteCreatedAt; // 笔记创建时间
}

