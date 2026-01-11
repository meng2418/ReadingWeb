package com.weread.dto.reading;

public class RecentBookDTO {
    private Integer bookId;
    private String cover; // 封面URL

    public RecentBookDTO() {
    }

    public RecentBookDTO(Integer bookId, String cover) {
        this.bookId = bookId;
        this.cover = cover;
    }

    // getters and setters
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
