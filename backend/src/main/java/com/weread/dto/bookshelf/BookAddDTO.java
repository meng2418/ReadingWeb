package com.weread.dto.bookshelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for adding a book to the bookshelf.
 */
@Data
public class BookAddDTO {

    /**
     * Book ID (required).
     */
    @NotNull(message = "图书ID不能为空")
    private Integer bookId;

    /**
     * Initial reading status (optional, default is reading).
     * Allowed values: "reading", "unread", "finished".
     */
    @NotBlank(message = "阅读状态不能为空")
    private String status = "reading";
}