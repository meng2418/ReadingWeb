package com.weread.entity.asset;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_package")
@Data
public class MembershipPackageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;
    
    @Column(name = "packageName", nullable = false, length = 100)
    private String packageName;
    
    @Column(name = "duration_type", nullable = false, length = 20)
    private String durationType;  // week, month, quarter, year
    
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
    
    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(name = "discount_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountPrice;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "is_hot")
    private Boolean isHot = false;
    
    @Column(name = "is_active")
    private Integer isActive = 1;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(name = "benefits", length = 1000)
    private String benefits;  // JSON格式存储权益列表
    
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