package com.weread.entity.asset;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
// 【✅ 修正：将唯一性约束放在 @Table 注解中】
@Table(name = "trial_reward_log", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "rewardType"})
})
public class TrialRewardLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String rewardType; 
    
    @Column(nullable = false)
    private Integer daysGranted;

    private LocalDateTime claimedAt = LocalDateTime.now();

}