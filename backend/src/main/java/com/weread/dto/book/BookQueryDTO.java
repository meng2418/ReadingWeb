package com.weread.dto.book;

import lombok.Data;

/**
 * 书籍查询DTO
 */
@Data
public class BookQueryDTO {

    private String keyword; // 关键词搜索

    private Integer categoryId; // 分类ID

    private Boolean isFree; // 是否免费

    private Boolean isMemberOnly; // 是否会员专属

    private Integer page = 0; // 页码（从0开始）

    private Integer size = 20; // 每页大小

    private String sortBy = "createdAt"; // 排序字段

    private String sortOrder = "DESC"; // 排序方向
}

