package com.weread.entity.author;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户关注作者实体
 */
@Entity
@Table(name = "user_follow_author",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "author_id"}))
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserFollowAuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 用户ID（关注者）
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * 作者ID（被关注者）
     */
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

