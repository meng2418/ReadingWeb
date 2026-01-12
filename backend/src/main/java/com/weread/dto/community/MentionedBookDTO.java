package com.weread.dto.community;

import lombok.Data;

@Data
public class MentionedBookDTO {
    private Integer bookId;
    private String cover;
    private String bookTitle;
    private String authorName;
    private String description;
}
