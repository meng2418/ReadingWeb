package com.weread.entity.community;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comment")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "post_id", nullable = false)
    private Long postId; // 所属帖子ID

    @Column(name = "user_id", nullable = false)
    private Long userId; // 评论者ID

    @Column(name = "parent_comment_id") 
    private Long parentCommentId; // 针对哪条评论的回复 (如果为 null，则是一级评论)
    
    @Lob 
    private String content; 

    private long likesCount = 0; // 评论的点赞数
    private int status = 0; // 状态 (0-正常, 1-删除)

    private int replyCount = 0;
    
    @CreatedDate
    private LocalDateTime createdAt;
}