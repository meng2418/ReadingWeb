package com.weread.vo.book;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍列表VO
 */
@Data
public class BookListVO {

    private Integer bookId;

    private String title;

    private String authorName;

    private String cover;

    private String description;

    private Integer categoryId;

    private String categoryName;

    private Integer price;

    private Boolean isFree;

    private Boolean isMemberOnly;

    private String tags;

    private Float rating;

    private Integer readCount;

    private LocalDateTime createdAt;
}

