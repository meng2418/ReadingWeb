package com.weread.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建书籍DTO
 */
@Data
public class BookCreateDTO {

    @NotBlank(message = "书籍标题不能为空")
    private String title;

    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    private String cover; // 封面URL

    private String description; // 简介

    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;

    private String publisher; // 出版社

    private String isbn; // ISBN号

    private Integer price = 0; // 价格（分）

    private Boolean isFree = false; // 是否免费

    private Boolean isMemberOnly = false; // 是否会员专属

    private String tags; // 标签（逗号分隔）
}

