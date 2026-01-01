package com.weread.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading_milestone")
@Data
public class ReadingMilestoneEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "milestone_id")
    private Long milestoneId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "milestone_type", nullable = false)
    private String milestoneType; // total_reading-累计阅读，note_count-笔记数量，finished_reading-读完本数
    
    @Column(name = "target_count", nullable = false)
    private Integer targetCount;
    
    @Column(name = "book_id")
    private Integer bookId;
    
    @Column(name = "book_title")
    private String bookTitle;
    
    @Column(name = "note_id")
    private Long noteId;
    
    @Column(name = "note_content_preview")
    private String noteContentPreview;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "is_latest", nullable = false)
    private Boolean isLatest = true;
    
    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}