package com.weread.dto.bookmark;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建书签DTO
 */
@Data
public class BookmarkCreateDTO {

    @NotNull(message = "书籍ID不能为空")
    private Integer bookId;

    @NotNull(message = "章节ID不能为空")
    private Integer chapterId;

    private Integer pageNumber; // 页码

    private String note; // 书签备注
}

