package com.weread.entity.community;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.weread.entity.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "topic_follows",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"}))
@Data
@EntityListeners(AuditingEntityListener.class)
public class TopicFollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tf_id")
    private Integer tfId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "topic_id", nullable = false)
    private Integer topicId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 可选：关联实体（仅用于查询，不参与插入/更新）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", insertable = false, updatable = false)
    private TopicEntity topic;
}