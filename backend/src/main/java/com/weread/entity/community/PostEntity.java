package com.weread.entity.community;

import com.weread.entity.base.BaseEntity;
import com.weread.entity.book.BookEntity;
import com.weread.entity.user.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Entity
@Table(name = "post_info")
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    // 关联用户 (author_id -> userId)
    @Column(name = "author_id", nullable = false)
    private Integer authorId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 发布地点
    @Column(name = "publish_location")
    private String publishLocation;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "share_count")
    private Integer shareCount = 0;

    // 状态：1-正常，2-隐藏，3-删除
    private Integer status = 1;

    @Column(name = "is_hot")
    private Boolean isHot = false;

    @Column(name = "is_top")
    private Boolean isTop = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private UserEntity user;

    // 关联书本
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_book_mapping", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<BookEntity> relatedBooks;

    // 【添加】通过中间表关联 TopicEntity
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTopicEntity> postTopics;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    // 软删除相关
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 原有字段保持兼容
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    // 【添加】便捷方法获取话题名称列表（用于接口返回）
    @Transient
    public List<String> getTopics() {
        if (postTopics != null && !postTopics.isEmpty()) {
            return postTopics.stream()
                    .map(postTopic -> postTopic.getTopic() != null ? postTopic.getTopic().getTopicName() : null)
                    .filter(topicName -> topicName != null && !topicName.trim().isEmpty())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    // 【添加】获取关联的 TopicEntity 列表
    @Transient
    public List<TopicEntity> getTopicEntities() {
        if (postTopics != null && !postTopics.isEmpty()) {
            return postTopics.stream()
                    .map(PostTopicEntity::getTopic)
                    .filter(topic -> topic != null)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    // 【修改】mentionedBooks 相关的方法
    @Transient
    public List<BookEntity> getMentionedBooks() {
        return relatedBooks != null ? relatedBooks : new ArrayList<>();
    }

    // 【添加】接口文档需要的其他方法
    @Transient
    public String getAuthorName() {
        return user != null ? user.getUsername() : null;
    }

    @Transient
    public String getAuthorAvatar() {
        return user != null ? user.getAvatar() : null;
    }

    @Transient
    public LocalDateTime getPublishTime() {
        return getCreatedAt();
    }

    public void setPublishLocation(String publishLocation) {
        this.publishLocation = publishLocation;
    }

    public String getPublishLocation() {
        return publishLocation;
    }

    @Transient
    public String getPostTitle() {
        return title;
    }

    @Transient
    public Integer getCommentCount() {
        return commentsCount;
    }

    @Transient
    public Integer getLikeCount() {
        return likesCount;
    }
}