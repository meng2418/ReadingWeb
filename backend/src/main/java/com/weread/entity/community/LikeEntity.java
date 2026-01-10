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
    private Integer likeId;

    @Column(name = "post_id")
    private Integer postId; 

    @Column(name = "comment_id")
    private Integer commentId; 

    @Column(name = "note_id")
    private Integer noteId; 

    @Column(name = "user_id", nullable = false)
    private Integer userId; 

    @Column(name = "target_type")
    private String targetType;

    @Transient
    private Integer targetId;

    // 业务方法：获取目标ID
    public Integer getTargetId() {
        if (postId != null) {
            return postId;
        } else if (commentId != null) {
            return commentId;
        }
        return null;
    }
    
    // 业务方法：获取目标类型
    public String getTargetType() {
        if (postId != null) {
            return "post";
        } else if (commentId != null) {
            return "comment";
        }
        return null;
    }
    
    @CreatedDate
    private LocalDateTime createdAt;
}