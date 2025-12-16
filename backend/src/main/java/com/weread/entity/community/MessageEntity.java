package com.weread.entity.community;

import com.weread.entity.user.UserEntity;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message_info") // 对应 Prisma 的 Message_info
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    // 消息接收者ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 消息类型: system, like, comment, follow (对应 Prisma 的 type)
    @Column(nullable = false)
    private String type;

    private String title;
    
    @Column(columnDefinition = "TEXT") // content 字段可能较长
    private String content;

    @Column(nullable = false)
    private Boolean isRead = false; // 是否已读

    private Long relatedId; // 相关实体ID (如 PostId, CommentId 等)

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 关联关系：接收者用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user; 
}