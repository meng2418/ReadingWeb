package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书架书籍展示VO（整合书籍、书架状态、阅读进度信息）
 */
@Data // 关键：Lombok的@Data注解会自动生成所有属性的getter、setter、toString等方法
public class BookShelfVO {
    // 书籍基础信息
    private Integer bookId;
    private String title;
    private String author;
    private String coverUrl;

    // 书架关联信息（来自BookshelfEntity）
    private String status; // 阅读状态：reading/unread/finished
    private LocalDateTime addedAt; // 添加到书架的时间
    private LocalDateTime lastReadTime; // 最后阅读时间

    // 阅读进度信息（来自ReadingProgressEntity）
    private Integer chapterId; // 当前章节ID
    private Integer currentPage; // 当前页码
    private Float progress; // 阅读进度（0-1）
}