package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 阅读进度更新结果 VO
 */
@Data 
public class ReadingProgressVO {
    private Integer bookId;
    private Integer chapterId;
    private Integer currentPage;
    private Float progress; // 阅读进度 (0-1)
    private LocalDateTime lastReadTime;
    private String message;
}