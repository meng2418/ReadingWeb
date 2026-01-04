package com.weread.entity.user;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Follow_info",
       // 确保一个用户不能重复关注另一个用户
       uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@Data
@EntityListeners(AuditingEntityListener.class)
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer followId;

    // 关注者 ID：发起关注行为的用户
    @Column(name = "follower_id", nullable = false)
    private Integer followerId;

    // 被关注者 ID：被关注的用户
    @Column(name = "following_id", nullable = false)
    private Integer followingId;

    // 【添加】关联实体 - 仅用于查询，不参与插入/更新
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private UserEntity followerUser;  // 关注者用户实体
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", insertable = false, updatable = false)
    private UserEntity followingUser; // 被关注者用户实体

    @CreatedDate
    private LocalDateTime createdAt;

    // 【修改】方法从关联实体获取信息
    public String getUsername() {
        return followingUser != null ? followingUser.getUsername() : null;
    }

    public String getAvatar() {
        return followingUser != null ? followingUser.getAvatar() : null;
    }

    public String getBio() {
        return followingUser != null ? followingUser.getBio() : null;
    }
    
    // 【添加】获取关注者用户名的便捷方法
    public String getFollowerUsername() {
        return followerUser != null ? followerUser.getUsername() : null;
    }
    
    public String getFollowerAvatar() {
        return followerUser != null ? followerUser.getAvatar() : null;
    }
}