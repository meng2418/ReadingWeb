package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 阅读进度更新结果视图对象
 */
@Data
public class ReadingProgressVO {
    private Integer bookId;
    private Integer chapterIndex; // 最新章节索引
    private Integer pageNum; // 最新页码
    private LocalDateTime lastReadTime; // 最新阅读时间
    private String message; // 操作提示信息
}