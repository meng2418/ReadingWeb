package com.weread.dto.bookshelf;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 阅读进度更新结果VO
 */
@Data // Lombok 自动生成 getter/setter，若未用 Lombok 需手动添加
public class ReadingProgressVO {
    private Integer bookId;
    private Integer chapterId;
    private Integer currentPage;
    private Float progress; // 阅读进度（0-1）
    private LocalDateTime lastReadTime;
    private String message;

    // 若未使用 Lombok @Data，需手动添加以下 setter 方法
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

    public void setLastReadTime(LocalDateTime lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}