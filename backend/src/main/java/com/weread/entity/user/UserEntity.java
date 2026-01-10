package com.weread.entity.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_info")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", unique = true, nullable = false, length = 255)
    private String username;

    @Column(name = "phone", unique = true, length = 255)
    private String phone;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "avatar", length = 255)
    private String avatar = "../../../../../data/pictures/default_avatar.jpg";

    @Column(name = "bio", length = 255)
    private String bio = "这个人很懒，什么都没有写";

    @Column(name = "is_member", nullable = false)
    private Boolean isMember = false;

    @Column(name = "member_end_date", nullable = false)
    private LocalDateTime memberEndDate=LocalDateTime.now();

    @Column(name = "coins", nullable = false)
    private Integer coins = 0;

    @Column(name = "total_reading_time", nullable = false)
    private Integer totalReadingTime = 0;

    @Column(name = "follower_count")
    private Integer followerCount = 0;

    // 【关键修复：添加 followingCount 属性】
    @Column(name = "following_count")
    private Integer followingCount = 0; // 必须添加此行

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "membership_expire_at")
    private LocalDateTime membershipExpireAt;

}
