package com.weread.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_reading_record")
@Data
public class UserReadingRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "book_title")
    private String bookTitle;

    @Column(name = "read_date", nullable = false)
    private LocalDate readDate;

    @Column(name = "reading_time", nullable = false)
    private Integer readingTime; // 阅读时长（分钟）

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "chapter_id")
    private Integer chapterId;

    @Column(name = "chapter_title")
    private String chapterTitle;

    @Column(name = "record_type")
    private Integer recordType; // 1-阅读记录，2-完成记录

    @Column(name = "created_at")
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