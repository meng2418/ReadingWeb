package com.weread.dto.bookshelf;

import lombok.Data;

/**
 * 查询书架书籍的请求参数（支持筛选和分页）
 */
@Data
public class BookshelfQueryDTO {

    /**
     * 按阅读状态筛选（可选，不传则查询所有状态）
     * 允许值：reading、unread、finished
     */
    private String status;

    /**
     * 页码（可选，默认第1页）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数（可选，默认10条）
     */
    private Integer pageSize = 10;
}