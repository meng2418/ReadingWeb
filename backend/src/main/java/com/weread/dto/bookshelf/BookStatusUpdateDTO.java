package com.weread.dto.bookshelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新书籍阅读状态的请求参数
 */
@Data
public class BookStatusUpdateDTO {

    /**
     * 书籍ID（必传）
     */
    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;

    /**
     * 新的阅读状态（必传）
     * 允许值：reading（阅读中）、unread（未读）、finished（已完成）
     */
    @NotBlank(message = "阅读状态不能为空")
    private String status;
}