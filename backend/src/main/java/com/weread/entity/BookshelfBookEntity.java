package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书架-书籍关联实体（对应数据库表 bookshelf_info）
 * 记录用户与书籍的关联关系、阅读状态及进度
 */
@Data
@Entity
@Table(name = "bookshelf_info", // 表名与数据库保持一致
        // 唯一约束：同一用户不能重复添加同一本书
        uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "book_id" }))
public class BookshelfBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookshelfid") // 对应数据库主键 bookshelfid
    private Integer id; // 关联记录ID（主键）

    @Column(name = "user_id", nullable = false)
    private Integer userId; // 关联用户ID（外键，替代原 shelf_id）

    @Column(name = "book_id", nullable = false)
    private Integer bookId; // 书籍ID（外键，保持不变）

    // 新增：阅读状态（对应数据库 status 字段）
    @Column(nullable = false)
    private String status = "reading"; // 默认为 reading（阅读中）

    // 保留：阅读进度相关字段
    @Column(name = "chapter_index")
    private Integer chapterIndex; // 当前阅读章节索引

    @Column(name = "page_num")
    private Integer pageNum; // 当前阅读页码

    // 时间字段调整：与数据库对应
    @Column(name = "last_read_at")
    private LocalDateTime lastReadTime; // 最后阅读时间（对应数据库 lastReadAt）

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addTime; // 添加到书架的时间（对应数据库 addedAt）

    // 关联关系调整：关联用户表而非书架表
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user; // 关联用户实体

    // 书籍关联保持不变
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private BookEntity book;

    // 自动填充时间和默认值
    @PrePersist
    protected void onCreate() {
        addTime = LocalDateTime.now(); // 初始化添加时间
        lastReadTime = LocalDateTime.now(); // 初始化为添加时间
        // status 已设置默认值，无需额外处理
    }
}