package com.weread.vo.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopBooksVO {
    
    private String period; // week, month, year, total
    private TopBookItem[] topBooks;
    
    @Data
    public static class TopBookItem {
        private Integer bookId;
        private String cover;
        private String bookTitle;
        private Integer readingTime; // 阅读时长（分钟）
        private String authorName;
        private Integer categoryId;
    }
}