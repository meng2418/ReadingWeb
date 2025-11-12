package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书架实体类（对应数据库表 bookshelf_info）
 * 用于记录用户与书籍的关联关系，包含阅读状态、时间等信息
 */
@Data
@Entity
@Table(name = "bookshelf_info",
        // 唯一约束：同一用户不能重复添加同一本书（匹配数据库的 @@unique([userId, bookId])）
        uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "book_id" }))
public class BookshelfEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookshelfid")
    private Integer bookshelfId; // 书架记录ID（主键，自增）

    @Column(name = "user_id", nullable = false)
    private Integer userId; // 关联用户ID（外键）

    @Column(name = "book_id", nullable = false)
    private Integer bookId; // 关联书籍ID（外键）

    @Column(nullable = false)
    private String status; // 阅读状态：reading（阅读中）、unread（未读）、finished（已完成），默认reading

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt; // 添加到书架的时间，默认当前时间

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt; // 最后阅读时间，可为null

    // 关联关系（按需添加，建议懒加载）
    /**
     * 与用户表的关联（多对一：多个书架记录属于一个用户）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user; // 关联的用户实体

    /**
     * 与书籍表的关联（多对一：多个书架记录关联同一本书）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private BookEntity book; // 关联的书籍实体

    // 自动填充默认值
    @PrePersist
    protected void onCreate() {
        // 设置添加时间为当前时间（若未手动设置）
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
        // 设置默认阅读状态为reading
        if (status == null || status.trim().isEmpty()) {
            status = "reading";
        }
    }
}