package com.weread.entity.user;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "achievement_log", uniqueConstraints = {
    // 确保同一用户不会重复记录同一类型的同一级成就
    @UniqueConstraint(columnNames = {"userId", "type", "level"}) 
})
public class AchievementLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type; // 成就类型：BOOK_COUNT, NOTE_COUNT, DURATION

    @Column(nullable = false)
    private Integer level; // 成就级别 (例如：读完 20 本书就是 Level 1)

    @Column(nullable = false)
    private String description; // 成就描述 (如: "累计阅读20本书")

    @Column(nullable = false)
    private Integer valueSnapshot; // 达成时的数值快照 (如：20本书, 50条笔记, 100分钟)

    private LocalDateTime achievedAt = LocalDateTime.now(); // 达成时间

}