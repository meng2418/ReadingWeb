package com.weread.entity.asset;
import com.weread.entity.user.UserEntity;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trial_reward_log", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "rewardType"})
})
public class TrialRewardLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String rewardType; 
    
    @Column(nullable = false)
    private Integer daysGranted;

    private LocalDateTime claimedAt = LocalDateTime.now();

    // 注意：如果您希望在日志中通过 JPA 关联到 UserEntity，需要添加 ManyToOne 关联：
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;
    

}