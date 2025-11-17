package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍实体类（对应数据库表 book_info）
 */
@Data
@Entity
@Table(name = "book_info") // 与数据库表名保持一致
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    @Column(name = "book_id") // 映射数据库字段 bookId
    private Integer bookId;

    @Column(nullable = false) // 非空字段
    private String title; // 书籍标题

    @Column(name = "author_id", nullable = false) // 外键字段，关联作者表
    private Integer authorId;

    private String cover; // 封面图片（允许为null）

    @Column(columnDefinition = "TEXT") // 长文本类型，对应数据库的@db.Text
    private String description; // 书籍简介（允许为null）

    @Column(name = "category_id", nullable = false) // 外键字段，关联分类表
    private Integer categoryId;

    private String publisher; // 出版社（允许为null）

    @Column(name = "publish_date")
    private LocalDateTime publishDate; // 出版日期（允许为null）

    private String isbn; // ISBN编号（允许为null）

    @Column(name = "word_count")
    private Integer wordCount; // 字数

    private Integer price; // 书币价格，默认0

    @Column(name = "is_free")
    private Boolean isFree; // 是否免费，默认false

    private Float rating; // 推荐值，默认0

    @Column(name = "read_count")
    private Integer readCount; // 阅读人数，默认0

    @Column(name = "created_at", updatable = false) // 不允许更新
    private LocalDateTime createdAt; // 创建时间，默认当前时间

    // 自动填充创建时间（如果数据库未设置默认值）
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // 初始化默认值（如果数据库未设置默认值）
        if (price == null) {
            price = 0;
        }
        if (isFree == null) {
            isFree = false;
        }
        if (rating == null) {
            rating = 0f;
        }
        if (readCount == null) {
            readCount = 0;
        }
    }
}