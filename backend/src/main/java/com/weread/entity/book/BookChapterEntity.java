package com.weread.entity.book;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍章节实体（对应数据库表 book_chapter）
 */
@Data
@Entity
@Table(name = "book_chapter")
public class BookChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Integer chapterId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId; // 外键，关联书籍

    @Column(nullable = false)
    private String title; // 章节标题

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber; // 章节序号（用于排序）

    @Column(columnDefinition = "TEXT")
    private String content; // 章节正文（大文本）

    @Column(name = "word_count")
    private Integer wordCount; // 本章字数（保存时自动计算）

    @Column(name = "is_vip")
    private Boolean isVip = false; // 是否VIP章节

    @Column(name = "is_published")
    private Boolean isPublished = true; // 是否已发布（草稿/发布）

    @Column(name = "prev_chapter_id")
    private Integer prevChapterId; // 上一章ID（用于链表结构）

    @Column(name = "next_chapter_id")
    private Integer nextChapterId; // 下一章ID（用于链表结构）

    @Column(name = "read_count")
    private Integer readCount = 0; // 本章阅读人数

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 最后更新时间

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // 初始化默认值
        if (wordCount == null) {
            wordCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}