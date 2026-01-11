package com.weread.entity.bookshelf;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.weread.entity.book.BookEntity;
import com.weread.entity.user.UserEntity;

/**
 * 书架实体类（对应数据库 bookshelf_info）
 */
@Data
@Entity
@Table(name = "bookshelf_info",
        // 唯一约束：同一用户不能重复添加同一本书
        uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "book_id" }))
public class BookshelfEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookshelf_id")
    private Integer bookshelfId; // 主键：保持 Integer

    @Column(name = "user_id", nullable = false)
    private Long userId; // 核心修正：必须是 Long，引用 UserEntity

    @Column(name = "book_id", nullable = false)
    private Integer bookId; // 外键：保持 Integer，引用 BookEntity

    @Column(nullable = false)
    private String status; // 阅读状态：reading, unread, finished，默认 reading

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt; // 添加到书架的时间

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt; // 最后阅读时间

    // 关联关系：

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user; // 关联用户实体

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private BookEntity book; // 关联书籍实体

    // 自动设置默认值
    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
        if (status == null || status.trim().isEmpty()) {
            status = "reading";
        }
    }
}