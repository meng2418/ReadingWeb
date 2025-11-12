package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书架书籍信息视图对象（用于前端展示书架中的书籍及阅读进度）
 */
@Data
public class BookShelfVO {
    private Integer bookId;
    private String title;
    private String author;
    private String coverUrl;
    private String status; // 阅读状态：reading/unread/finished
    private Integer chapterIndex; // 当前阅读章节索引
    private LocalDateTime lastReadTime; // 最后阅读时间
}