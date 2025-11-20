package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chapter_info")
public class ChapterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapterid")
    private Integer chapterId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer order;

    @Column(name = "bookid", nullable = false)
    private Integer bookId;

    @Column(name = "createdat")
    private LocalDateTime createdAt = LocalDateTime.now();

}