package com.weread.entity.asset;

import com.weread.entity.user.UserEntity;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trial_balance_info")
public class TrialBalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    @Column(unique = true, nullable = false)
    private Long userId; // 关联到 UserEntity 的 ID

    private Integer daysBalance = 0; // 剩余体验天数余额

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // 关联关系：如果需要从 Balance 查 User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;
}