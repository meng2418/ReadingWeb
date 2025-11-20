package com.weread.dto.bookshelf;

import lombok.Data;

/**
 * 阅读进度更新DTO
 */
@Data // Lombok 自动生成 getter getter/setter，若未用 Lombok 需手动添加
public class ReadingProgressDTO {
    private Integer bookId;
    private Integer chapterId; // 当前阅读章节ID
    private Integer currentPage; // 当前页码
    private Float progress; // 阅读进度（0-1）

    // 若未使用 Lombok @Data，需手动手动以下 getter 方法
    public Integer getBookId() {
        return bookId;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public Float getProgress() {
        return progress;
    }
}