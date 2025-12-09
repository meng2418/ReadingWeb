package com.weread.entity.community;
import com.weread.entity.user.UserEntity;
import com.weread.entity.note.NoteEntity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment_info")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "post_id", nullable = false)
    private Long postId; // 所属帖子ID

    @Column(name = "note_id")
    private Integer noteId; // 保持 Integer (笔记 ID 是内容资产 ID)

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

    // 💡 提示：如果需要 JPA 关联，您可以添加：
    
    // --- JPA 关联 ---

    // 关联评论用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;

    // 关联所属帖子
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId", insertable = false, updatable = false)
    private PostEntity post;

    // 关联所属笔记 (如果 noteId 不为空)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noteId", referencedColumnName = "noteId", insertable = false, updatable = false)
    private NoteEntity note;
    
    // 关联父级评论 (自引用)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCommentId", referencedColumnName = "commentId", insertable = false, updatable = false)
    private CommentEntity parentComment;

    
}