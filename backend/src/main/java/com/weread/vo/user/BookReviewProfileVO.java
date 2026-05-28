package com.weread.vo.user;

import lombok.Data;

@Data
public class BookReviewProfileVO {
    private Integer id;
    private String bookName;
    private String cover;
    private String rating;
    private String date;
    private Integer likes;
    private String content;
}
