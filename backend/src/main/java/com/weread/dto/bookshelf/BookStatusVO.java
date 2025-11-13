package com.weread.dto.bookshelf;

import lombok.Data;

/**
 * 书籍状态更新结果视图对象
 */
@Data
public class BookStatusVO {
    private Integer bookId;
    private String status; // 新的阅读状态
    private String message; // 操作提示信息
}