package com.weread.entity.book;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍分类实体（对应数据库表 book_category）
 * 支持多级分类结构
 */
@Data
@Entity
@Table(name = "book_category")
public class BookCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(nullable = false)
    private String name; // 分类名称

    @Column(name = "parent_id")
    private Integer parentId; // 父分类ID（0表示顶级分类）

    @Column(name = "category_level")
    private Integer categoryLevel = 1; // 分类层级（1,2,3...）

    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序权重（越大越靠前）

    private String icon; // 分类图标URL

    @Column(columnDefinition = "TEXT")
    private String description; // 分类描述

    @Column(name = "book_count")
    private Integer bookCount = 0; // 该分类下书籍数量（缓存字段）

    @Column(name = "is_enabled")
    private Boolean isEnabled = true; // 是否启用

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}