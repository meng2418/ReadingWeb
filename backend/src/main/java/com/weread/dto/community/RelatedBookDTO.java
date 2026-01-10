package com.weread.dto.community;

import lombok.Data;

@Data
public class RelatedBookDTO {
    private Integer bookId;
    private String cover;
    private String title;
    private String author;
    private String description;
}