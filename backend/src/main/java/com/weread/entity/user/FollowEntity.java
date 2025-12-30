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

    @CreatedDate
    private LocalDateTime createdAt;
}