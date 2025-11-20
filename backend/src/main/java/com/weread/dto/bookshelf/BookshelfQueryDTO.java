package com.weread.dto.bookshelf;

import lombok.Data;

/**
 * 查询书架书籍的请求参数（仅支持状态筛选，无分页）
 */
@Data
public class BookshelfQueryDTO {

    /**
     * 按阅读状态筛选（可选，不传则查询所有状态）
     * 允许值：reading、unread、finished
     */
    private String status;
}