package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Response view object for adding a book to the bookshelf (corresponds to BookAddDTO).
 */
@Data
public class BookAddVO {
    // Book basic information (which the frontend may need to display the newly added book)
    private Integer bookId;
    private String title;
    private String author;
    private String coverUrl;

    // Additional information
    private String status; // Initial reading status (e.g., "reading")
    private LocalDateTime addedAt; // Time when the book was added to the shelf
    private String message; // Message to display (e.g., "Book added to bookshelf successfully")
}