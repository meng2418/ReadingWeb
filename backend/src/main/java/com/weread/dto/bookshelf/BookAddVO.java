package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 添加书籍到书架的响应视图对象（对应 BookAddDTO）
 */
@Data
public class BookAddVO {
    // 书籍基础信息（前端可能需要展示刚添加的书籍信息）
    private Integer bookId;
    private String title;
    private String author;
    private String coverUrl;

    // 添加结果信息
    private String status; // 初始阅读状态（如 reading）
    private LocalDateTime addedAt; // 添加到书架的时间
    private String message; // 操作提示（如 "书籍已成功添加到书架"）
}