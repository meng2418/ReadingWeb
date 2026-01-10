package com.weread.vo.book;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍详情VO
 */
@Data
public class BookDetailVO {

    private Integer bookId;

    private String title;

    private Integer authorId;

    private String authorName;

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

    private String tags;

    private Float rating;

    private Integer readCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

