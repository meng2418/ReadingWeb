package com.weread.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建章节DTO
 */
@Data
public class ChapterCreateDTO {

    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;

    @NotBlank(message = "章节标题不能为空")
    private String title;

    @NotNull(message = "章节序号不能为空")
    private Integer chapterNumber;

    @NotBlank(message = "章节内容不能为空")
    private String content;

    private Boolean isVip = false; // 是否VIP章节

    private Boolean isPublished = true; // 是否发布
}

