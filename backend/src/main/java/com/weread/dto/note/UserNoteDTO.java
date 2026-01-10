package com.weread.dto.note;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户笔记DTO（用于笔记瀑布流接口）
 * 对应接口：GET /user/notes
 */
@Data
public class UserNoteDTO {
    private Long markId; // 笔记ID（对应noteId）
    private Integer bookId;
    private String bookTitle;
    private Integer chapterId;
    private String chapterName;
    private String quote; // 原文引用内容
    private Integer startIndex; // 笔记起始位置（字符索引）
    private Integer endIndex; // 笔记结束位置（字符索引）
    private Integer pageNumber; // 笔记页码
    private List<String> lineTypes; // 划线类型数组（marker、wavy、underline）
    private String noteContent; // 想法内容
    private LocalDateTime noteCreatedAt; // 笔记创建时间
    private String noteType; // "highlight" 或 "thought"
}

