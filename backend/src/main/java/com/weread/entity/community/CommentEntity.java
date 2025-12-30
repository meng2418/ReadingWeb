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
    private Integer commentId;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    
    @Column(name = "note_id")
    private Integer noteId; 

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "parent_comment_id")
    private Integer parentCommentId;

    @Lob
    private String content;

    private int likesCount = 0;
    private int status = 0;
    private int replyCount = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    // --- 关联 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", insertable = false, updatable = false)
    private NoteEntity note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", insertable = false, updatable = false)
    private CommentEntity parentComment;
}