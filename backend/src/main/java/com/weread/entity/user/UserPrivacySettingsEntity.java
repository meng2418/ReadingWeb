package com.weread.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户隐私设置，映射表 user_privacy_settings。
 */
@Data
@Entity
@Table(name = "user_privacy_settings",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class UserPrivacySettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(nullable = false)
    private Boolean bookshelf = true;

    @Column(name = "reading_stats", nullable = false)
    private Boolean readingStats = true;

    @Column(nullable = false)
    private Boolean highlights = true;

    @Column(nullable = false)
    private Boolean thoughts = true;

    @Column(name = "book_reviews", nullable = false)
    private Boolean bookReviews = true;

    @Column(nullable = false)
    private Boolean followers = true;

    @Column(nullable = false)
    private Boolean following = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        applyDefaults();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        applyDefaults();
    }

    private void applyDefaults() {
        if (bookshelf == null) bookshelf = true;
        if (readingStats == null) readingStats = true;
        if (highlights == null) highlights = true;
        if (thoughts == null) thoughts = true;
        if (bookReviews == null) bookReviews = true;
        if (followers == null) followers = true;
        if (following == null) following = true;
    }
}
