package com.weread.dto.reading;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 上报阅读记录请求（阅读时长、当前章节等）
 */
@Data
public class AddReadingRecordRequestDTO {

    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;

    private String bookTitle;

    /** 本次阅读时长（分钟） */
    @NotNull(message = "阅读时长不能为空")
    private Integer readingTime;

    private Integer pageCount;

    /** 当前读到的章节ID */
    private Integer chapterId;

    private String chapterTitle;
}
