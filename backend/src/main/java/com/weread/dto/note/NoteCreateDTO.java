package com.weread.dto.note;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发布笔记请求DTO
 */
@Data
public class NoteCreateDTO {

    @NotNull(message = "书籍ID不能为空")
    @Min(value = 1, message = "书籍ID必须大于0")
    private Integer bookId;

    private Integer chapterId;

    @NotBlank(message = "引用内容不能为空")
    private String quote;

    private String lineType; // marker, wavy, underline，想法类型时为 null

    private String thought; // 想法/笔记内容

    @Min(value = 0, message = "rangeStart 必须大于等于0")
    private Integer rangeStart = 0; // 笔记在当前章节原文中的起始索引

    @Min(value = 0, message = "rangeEnd 必须大于等于0")
    private Integer rangeEnd = 0; // 笔记在当前章节原文中的结束索引
}

