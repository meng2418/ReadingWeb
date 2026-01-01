package com.weread.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading_reward")
@Data
public class ReadingRewardEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long rewardId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "reward_date", nullable = false)
    private LocalDate rewardDate;
    
    @Column(name = "reward_type", nullable = false)
    private String rewardType; // daily-每日激励，weekly-每周激励
    
    @Column(name = "reward_value")
    private Integer rewardValue; // 奖励值（如体验卡天数）
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "is_claimed")
    private Boolean isClaimed = false;
    
    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}