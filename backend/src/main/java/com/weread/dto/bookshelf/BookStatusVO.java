package com.weread.dto.bookshelf;

import lombok.Data;

/**
 * 书架状态更新结果视图对象 (View Object).
 */
@Data
public class BookStatusVO {
    private Integer bookId;
    private String status; // 新的阅读状态
    private String message; // 成功或失败的提示信息
}