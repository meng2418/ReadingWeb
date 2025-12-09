package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "readingprogress_info", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "book_id" }))
public class ReadingProgressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readingprogressid")
    private Integer readingProgressId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "chapterid")
    private Integer chapterId;

    @Column(name = "currentpage")
    private Integer currentPage = 1;

    private Float progress = 0f; // 0-1

    @Column(name = "lastreadat")
    private LocalDateTime lastReadAt = LocalDateTime.now();

}