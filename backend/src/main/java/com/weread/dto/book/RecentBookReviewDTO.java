package com.weread.dto.book;

import lombok.Data;

/**
 * 最新书评DTO（用于获取登录用户最新的4条书评）
 * 对应接口：GET /user/book-reviews/recent
 * 符合OpenAPI定义的BookReview schema
 */
@Data
public class RecentBookReviewDTO {
    private String cover;           // 书籍封面URL
    private String bookTitle;       // 书籍标题
    private String reviewDate;      // 书评日期（格式：YYYY-MM-DD）
    private Integer helpfulCount;   // 有用数（点赞数），minimum: 1
    private String reviewContent;   // 书评内容
    private String rating;          // 评分：recommend, average, not_recommend
}

