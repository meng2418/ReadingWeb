package com.weread.entity.community;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
// 确保同一用户对同一目标（帖子/评论）只能点赞一次
@Table(name = "like_info", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "postId", "commentId", "noteId"}))
@Data
@EntityListeners(AuditingEntityListener.class)
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column(name = "post_id")
    private Long postId; // 核心修改：帖子 ID 必须是 Long

    @Column(name = "comment_id")
    private Long commentId; // 核心修改：评论 ID 必须是 Long
    
    @Column(name = "note_id")
    private Integer noteId; // 保持 Integer (笔记 ID 是内容资产 ID)

    @Column(name = "user_id", nullable = false)
    private Long userId; // 点赞的用户ID
    
    @CreatedDate
    private LocalDateTime createdAt;
}