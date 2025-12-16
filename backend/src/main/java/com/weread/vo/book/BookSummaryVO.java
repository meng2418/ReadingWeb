package com.weread.vo.book;

import lombok.Data;

/**
 * 书籍摘要展示对象 (Book Summary Value Object) - 极简版
 * 专用于帖子关联书籍时的简要信息展示。
 */
@Data
public class BookSummaryVO {

    private Integer bookId;

    /** 书籍标题 */
    private String title;

    /** 封面图片URL */
    private String cover;

    /** 作者姓名 */
    private String authorName;

    /** 简介/摘要 */
    private String description; // 直接使用完整的 description 字段
}