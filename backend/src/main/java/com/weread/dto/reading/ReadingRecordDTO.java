package com.weread.dto.reading;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 阅读记录DTO
 */
@Data
public class ReadingRecordDTO {

    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;

    @NotNull(message = "章节ID不能为空")
    private Integer chapterId;

    private Integer currentPage = 1; // 当前页码

    private Float progress = 0f; // 阅读进度（0-1）
}

