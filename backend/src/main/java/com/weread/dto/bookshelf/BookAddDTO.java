package com.weread.dto.bookshelf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加书籍到书架的请求参数
 */
@Data
public class BookAddDTO {

    /**
     * 书籍ID（必传）
     */
    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;

    /**
     * 初始阅读状态（可选，默认reading）
     * 允许值：reading（阅读中）、unread（未读）、finished（已完成）
     */
    @NotBlank(message = "阅读状态不能为空")
    private String status = "reading";
}