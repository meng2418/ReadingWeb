package com.weread.vo.book;

import lombok.Data;
@Data
public class MentionedBookVO {
    private String cover;
    private String bookTitle;
    private String authorName;
    private Integer authorId;
    private Integer rating;
    private Integer readCount;
    private String description;
    
    public void setRating(float f) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRating'");
    }
    public void setAuthorId(long l) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAuthorId'");
    }
}
