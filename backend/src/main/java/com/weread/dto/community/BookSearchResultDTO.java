package com.weread.dto.community;

import lombok.Data;

@Data
public class BookSearchResultDTO {
    private Integer bookId;
    private String bookTitle;
    private String authorName;
}