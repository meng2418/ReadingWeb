package com.weread.dto.community;

import lombok.Data;

@Data
public class BookSimpleDTO {
    private String cover;
    private String bookTitle;
    private String authorName;
    private Integer bookId;
}
