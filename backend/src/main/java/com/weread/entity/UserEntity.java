package com.weread.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "User_info")
@Data
public class UserEntity {
    // ---------------------- 基础信息 ----------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 11)
    private String phone; // 手机号

    @Column(unique = true, length = 50)
    private String username; // 用户名

    @Column(unique = true, length = 100)
    private String email; // 邮箱（可选）

    @Column(nullable = false, length = 100)
    private String password; // 加密后的密码
    
    // ---------------------- 档案信息 ----------------------
    
    @Column(length = 255)
    private String avatar; // 头像URL
    
    @Column(length = 500)
    private String bio; // 个人简介
    
    // ---------------------- 权益与统计 ----------------------
    @Column(nullable = false)
    private Boolean isMember = false; // 是否为会员（与 Member_info 表对应）
    
    @Column(nullable = false)
    private Integer coins = 0; // 书币余额
    
    @Column(nullable = false)
    private Integer totalReadingTime = 0; // 总阅读时长（分钟）
    
    // ---------------------- 时间戳 ----------------------
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 更新时间
}
