package com.weread.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建书评请求DTO
 */
@Data
public class BookReviewCreateDTO {

    @NotNull(message = "书籍ID不能为空")
    @Min(value = 1, message = "书籍ID必须大于0")
    private Integer bookId;

    @NotBlank(message = "评分不能为空")
    private String rating; // recommend, average, not_recommend

    @NotBlank(message = "评论内容不能为空")
    @Size(min = 10, max = 2000, message = "评论内容长度必须在10-2000字符之间")
    private String content;

    private Boolean isPublic = true; // 默认公开
}

