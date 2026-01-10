package com.weread.vo.book;

import lombok.Data;

/**
 * 简单书籍信息VO（SimpleBook）
 * 用于书评等场景中的书籍简要信息展示
 */
@Data
public class SimpleBookVO {
    /**
     * 封面图URL
     */
    private String cover;

    /**
     * 书名
     */
    private String bookTitle;

    /**
     * 作者名
     */
    private String authorName;

    /**
     * 作者ID
     */
    private Integer authorId;

    /**
     * 推荐值（0-100）
     */
    private Integer rating;

    /**
     * 今日阅读人数
     */
    private Integer readCount;

    /**
     * 作品简介
     */
    private String description;
}

