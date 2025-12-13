package com.weread.dto.bookshelf;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for batch operations on the bookshelf (corresponds to POST /bookshelf/batch).
 */
@Data
public class BookshelfBatchDTO {
    @NotNull(message = "操作类型不能为空")
    private String action; // Allowed values: "delete", "update-status"

    @NotEmpty(message = "图书ID列表不能为空")
    private List<Integer> bookIds;

    private String status; // Required when action is "update-status". Allowed values: "unread", "reading", "finished"
}