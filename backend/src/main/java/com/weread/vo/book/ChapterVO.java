package com.weread.vo.book;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 章节VO
 */
@Data
public class ChapterVO {

    private Integer chapterId;

    private Integer bookId;

    private String title;

    private Integer chapterNumber;

    private String content;

    private Integer wordCount;

    private Boolean isVip;

    private Boolean isPublished;

    private Integer prevChapterId;

    private Integer nextChapterId;

    private Integer readCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

