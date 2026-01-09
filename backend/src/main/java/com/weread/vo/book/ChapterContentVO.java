package com.weread.vo.book;

import lombok.Data;

/**
 * 章节内容VO（用于阅读器接口）
 * 匹配前端JSON接口定义的响应格式
 */
@Data
public class ChapterContentVO {

    private Integer chapterId;

    private String chapterTitle;

    private Integer chapterNumber;

    private String content;

    private Integer totalWords;

    private Integer totalPages;

    private Integer wordsPerPage;

    private Boolean requireExperienceCard;

    private Boolean isLocked;

    private Integer prevChapterId;

    private Integer nextChapterId;

    private Integer lastReadPosition;
}



