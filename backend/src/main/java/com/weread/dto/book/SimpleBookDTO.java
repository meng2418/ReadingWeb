package com.weread.dto.book;

import lombok.Data;

@Data
public class SimpleBookDTO {
    private Integer bookId;
    private String bookTitle; // 对应文档字段
    private String author;
    private String cover; // 对应文档字段
    private String readingStatus; // 对应文档字段
    private String description; // 作品简介

    private float rating; // 评分
    private Integer readCount; // 阅读数
}