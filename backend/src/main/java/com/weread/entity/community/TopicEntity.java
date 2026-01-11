package com.weread.entity.community;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "topic_info")
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicId;

    @Column(name = "topic_name", unique = true, nullable = false, length = 50)
    private String topicName; // 话题名称

    @Column(name = "description", length = 255)
    private String description = "暂无简介";

    // 新增字段：图片URL
    @Column(name = "image", length = 500)
    private String image = "../../../../../data/pictures/default_topic.jpg";
    
    // 新增字段：帖子数（可以缓存）
    @Column(name = "post_count")
    private Integer postCount = 0;
    
    // 新增字段：创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // 新增字段：更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "follower_count")
    private Integer followerCount = 0;

    @Column(name = "introduction")
    private String introduction = "暂无介绍";

    @Column(name = "admin_name")
    private String adminName = "管理员";

    // 关联到中间表
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTopicEntity> postTopics;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
}
