package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Bookshelf display VO (View Object), contains book details, status, and reading progress information.
 */
@Data // The Lombok @Data annotation automatically generates getters, setters, toString, etc.
public class BookShelfVO {
    // Book basic information
    private Integer bookId;
    private String title;
    private String author;
    private String coverUrl;

    // Bookshelf status information (from BookshelfEntity)
    private String status; // Reading status: "reading", "unread", "finished"
    private LocalDateTime addedAt; // Time when the book was added to the shelf
    private LocalDateTime lastReadTime; // Last reading time

    // Reading progress information (from ReadingProgressEntity)
    private Integer chapterId; // Current chapter ID
    private Integer currentPage; // Current page number
    private Float progress; // Reading progress (0.0 to 1.0)
}