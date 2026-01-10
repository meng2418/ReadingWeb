package com.weread.dto.book;

import lombok.Data;

/**
 * 书评项DTO（用于用户书评瀑布流）
 * 对应接口：GET /user/book-reviews
 */
@Data
public class BookReviewItemDTO {
    private String cover;           // 书籍封面
    private String bookTitle;       // 书籍标题
    private String reviewDate;      // 书评日期（格式：YYYY-MM-DD）
    private Integer helpfulCount;   // 有用数（点赞数）
    private String reviewContent;   // 书评内容
    private String rating;          // 评分：recommend, average, bad
}

