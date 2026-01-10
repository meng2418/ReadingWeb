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
    @Column(name = "readingprogress_id")
    private Integer readingProgressId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "chapter_id")
    private Integer chapterId;

    @Column(name = "currentpage")
    private Integer currentPage = 1;

    private Float progress = 0f; // 0-1 判断未读、正在读、读完

    @Column(name = "lastreadat")
    private LocalDateTime lastReadAt = LocalDateTime.now();

}