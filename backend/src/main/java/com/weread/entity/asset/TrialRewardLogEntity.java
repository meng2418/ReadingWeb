package com.weread.entity.asset;

import com.weread.entity.user.UserEntity;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trial_reward_log", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "reward_type"})
})
public class TrialRewardLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reward_type", nullable = false, length = 50)
    private String rewardType;

    @Column(name = "days_granted", nullable = false)
    private Integer daysGranted;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;
}
