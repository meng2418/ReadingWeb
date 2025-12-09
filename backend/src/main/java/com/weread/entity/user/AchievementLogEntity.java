package com.weread.entity.user;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "achievement_log", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "type", "level"}) 
})
public class AchievementLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer valueSnapshot;

    private LocalDateTime achievedAt = LocalDateTime.now();
}
