package com.weread.entity.note;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书签实体
 */
@Data
@Entity
@Table(name = "bookmark_info", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "book_id", "chapter_id" })
})
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Integer bookmarkId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "chapter_id", nullable = false)
    private Integer chapterId;

    @Column(name = "page_number")
    private Integer pageNumber; // 页码

    @Column(columnDefinition = "TEXT")
    private String note; // 书签备注

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
