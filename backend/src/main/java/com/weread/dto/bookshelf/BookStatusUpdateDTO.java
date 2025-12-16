package com.weread.dto.bookshelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for updating a book's reading status.
 */
@Data
public class BookStatusUpdateDTO {

    /**
     * Book ID (required).
     */
    @NotNull(message = "图书ID不能为空")
    private Integer bookId;

    /**
     * New reading status (required).
     * Allowed values: "reading", "unread", "finished".
     */
    @NotBlank(message = "阅读状态不能为空")
    private String status;
    
    /**
     * Time of last read (format: yyyy-MM-dd HH:mm:ss).
     */
    private String lastReadAt; 
}