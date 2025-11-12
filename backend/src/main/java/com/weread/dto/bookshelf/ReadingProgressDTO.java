package com.weread.dto.bookshelf;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新阅读进度的请求参数
 */
@Data
public class ReadingProgressDTO {

    /**
     * 书籍ID（必传）
     */
    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;

    /**
     * 当前阅读章节索引（必传，从0开始）
     */
    @NotNull(message = "章节索引不能为空")
    @Min(value = 0, message = "章节索引不能为负数")
    private Integer chapterIndex;

    /**
     * 当前阅读页码（可选，默认为0）
     */
    @Min(value = 0, message = "页码不能为负数")
    private Integer pageNum = 0;
}