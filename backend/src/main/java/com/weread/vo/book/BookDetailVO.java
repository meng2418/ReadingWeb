package com.weread.vo.book;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍详情VO
 */
@Data
public class BookDetailVO {

    // 基础信息
    private Integer bookId;
    private String bookTitle; // 前端期望的字段名
    private String title; // 兼容字段

    private Integer authorId;
    private String authorName;
    private String author; // 前端期望的字段名
    private String authorBio; // 作者简介

    private String cover;
    private String description;

    private Integer categoryId;
    private String categoryName;

    private String publisher;
    private LocalDateTime publishDate;

    private String isbn;
    private Integer wordCount;
    private Integer price;
    private Boolean isFree;
    private Boolean isPublished;
    private Boolean isMemberOnly;
    private Boolean isFreeForMember; // 体验卡是否可读（通常等于isMemberOnly）

    private String tags;
    private Integer rating; // 推荐值，范围0-100
    private Integer readCount;

    // 用户相关状态（需要根据当前用户查询）
    private Boolean isInBookshelf; // 是否加入书架
    private Boolean hasStarted; // 是否开始阅读
    private String readingStatus; // 阅读状态: not_started, reading, finished
    private Boolean isFinished; // 是否已读完

    // 统计信息
    private Integer readingCount; // 在读人数
    private Integer finishedCount; // 读完人数
    private Integer ratingCount; // 点评人数

    // 点评详情
    private RatingDetailVO ratingDetail; // 点评百分比详情

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 点评详情VO
     */
    @Data
    public static class RatingDetailVO {
        private Double recommendPercent; // 推荐百分比
        private Double averagePercent; // 一般百分比
        private Double notRecommendPercent; // 不行百分比
    }
}

