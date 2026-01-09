package com.weread.vo.book;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书评VO
 */
@Data
public class BookReviewVO {
    private Integer reviewId;
    private Integer bookId;
    private String bookTitle;
    private String bookCover;
    
    /**
     * 书籍信息对象（SimpleBook格式，用于发布书评接口返回）
     */
    private SimpleBookVO book;
    
    private Integer userId;
    private String username;
    private String avatar;
    private String rating; // recommend, average, not_recommend
    private String content;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime reviewTime; // 别名，用于前端兼容
}

