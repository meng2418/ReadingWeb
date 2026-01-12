package com.weread.dto.book;

import lombok.Data;

@Data
public class SimpleBookDTO {
    private Integer bookId;
    private String bookTitle;
    private String authorName;  // 修改：author -> authorName
    private Integer authorId;   // 新增字段
    private String cover;
    private String readingStatus;
    private String description;
    private Float rating;       // 建议使用 Float 而不是 float
    private Integer readCount;
}