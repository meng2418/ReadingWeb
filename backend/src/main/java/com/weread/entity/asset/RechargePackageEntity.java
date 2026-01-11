package com.weread.entity.asset;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_package")
@Data
public class RechargePackageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;
    
    @Column(name = "coin_amount", nullable = false)
    private Integer coinAmount;      // 充值币数
    
    @Column(name = "cny_amount", nullable = false)
    private Double cnyAmount;        // 人民币数
    
    @Column(name = "bonus_coins", nullable = false)
    private Integer bonusCoins;      // 赠送充值币数
    
    @Column(name = "is_active")
    private Integer isActive;  // 0 或 1
    
    @Column(name = "display_order")
    private Integer displayOrder;    // 显示顺序
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    

    
}