package com.weread.dto.note;

import lombok.Data;

/**
 * 最新笔记DTO（用于获取登录用户最新的6条笔记接口）
 * 对应接口：GET /user/notes/recent6
 */
@Data
public class BookNoteDTO {
    private String bookTitle;      // 书籍标题
    private String quote;          // 原文引用内容
    private String noteContent;   // 想法内容
    private String noteDate;      // 笔记日期（格式：YYYY-MM-DD）
}

