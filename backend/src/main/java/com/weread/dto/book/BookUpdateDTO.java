package com.weread.dto.book;

import lombok.Data;

/**
 * 更新书籍DTO
 */
@Data
public class BookUpdateDTO {

    private String title;

    private Long authorId;

    private String cover;

    private String description;

    private Integer categoryId;

    private String publisher;

    private String isbn;

    private Integer price;

    private Boolean isFree;

    private Boolean isPublished; // 上架/下架

    private Boolean isMemberOnly;

    private String tags;
}

