package com.weread.vo.book;

import lombok.Data;

@Data
public class BookVO {
    private Integer bookId;
    private String bookTitle;
    private String cover;
    private String authorName;
}