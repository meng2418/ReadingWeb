package com.weread.dto.bookshelf;

import lombok.Data;

/**
 * DTO for querying the bookshelf list (supports status filtering, no pagination).
 */
@Data
public class BookshelfQueryDTO {

    /**
     * Reading status filter (optional, if null, queries all statuses).
     * Allowed values: "reading", "unread", "finished"
     */
    private String status;
}