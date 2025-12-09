package com.weread.entity.community;

import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 帖子和话题的多对多关联实体（中间表）
 */
@Data
@Entity
@Table(name = "post_topic_info")
@IdClass(PostTopicEntity.PostTopicId.class) // 使用复合主键
public class PostTopicEntity implements Serializable {

    @Id
    @Column(nullable = false)
    private Long postId;

    @Id
    @Column(nullable = false)
    private Integer topicId; 

    // 关联到 PostEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId", insertable = false, updatable = false)
    private PostEntity post;

    // 关联到 TopicEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topicId", referencedColumnName = "topicId", insertable = false, updatable = false)
    private TopicEntity topic;

    // 复合主键类
    @Data
    public static class PostTopicId implements Serializable {
        private Long postId; 
        private Integer topicId; 
    }
}