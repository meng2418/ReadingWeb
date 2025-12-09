package com.weread.dto.bookshelf;

import lombok.Data;

/**
 * 阅读进度更新 DTO
 */
@Data 
public class ReadingProgressDTO {
    private Integer bookId;
    private Integer chapterId; // 当前阅读章节 ID
    private Integer currentPage; // 当前页码
    private Float progress; // 阅读进度 (0-1)
}