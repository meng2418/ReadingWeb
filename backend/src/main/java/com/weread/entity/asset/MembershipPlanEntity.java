package com.weread.entity.asset;

import com.weread.entity.base.BaseEntity; 
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

/**
 * 会员套餐的产品定义表，用于存储定价和有效期配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "membership_plan_info")
// 假设继承了 BaseEntity
public class MembershipPlanEntity extends BaseEntity { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planId;
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer durationDays; // 有效天数

    @Column(nullable = false)
    private Integer priceCents; // 价格（人民币分）

    @Column(nullable = false)
    private Boolean isAvailable = true; // 是否在售

    @Column(length = 255)
    private String description;
}