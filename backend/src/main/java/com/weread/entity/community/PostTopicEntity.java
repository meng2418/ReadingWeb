package com.weread.entity.community;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "post_topic_info")
@IdClass(PostTopicEntity.PostTopicId.class)
public class PostTopicEntity implements Serializable {

    @Id
    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Id
    @Column(name = "topic_id", nullable = false)
    private Integer topicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", insertable = false, updatable = false)
    private TopicEntity topic;

    @Data
    @NoArgsConstructor
    public static class PostTopicId implements Serializable {

        @Column(name = "post_id")
        private Integer postId;

        @Column(name = "topic_id")
        private Integer topicId;
    }
}
