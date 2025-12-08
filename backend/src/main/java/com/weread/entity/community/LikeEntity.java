package com.weread.entity.community;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
// 确保同一用户对同一目标（帖子/评论）只能点赞一次
@Table(name = "user_like", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"target_type", "target_id", "user_id"})) 
@Data
@EntityListeners(AuditingEntityListener.class)
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    // 点赞目标类型：例如 "POST", "COMMENT"
    @Column(name = "target_type", length = 10, nullable = false)
    private String targetType; 

    // 点赞目标的ID：帖子ID 或 评论ID
    @Column(name = "target_id", nullable = false)
    private Long targetId; 

    @Column(name = "user_id", nullable = false)
    private Long userId; // 点赞的用户ID
    
    @CreatedDate
    private LocalDateTime createdAt;
}