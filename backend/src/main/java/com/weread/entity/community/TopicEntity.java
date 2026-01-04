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

    @Column(length = 255)
    private String description;

    // 新增字段：图片URL
    @Column(name = "image", length = 500)
    private String image;
    
    // 新增字段：帖子数（可以缓存）
    @Column(name = "post_count")
    private Integer postCount = 0;
    
    // 新增字段：创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 新增字段：更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "view_count")
    private Integer viewCount = 0;

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

    // 【修改】修复这些方法
    public Integer getFollowerCount() {
        // 这里可以返回一个默认值，或者从数据库查询
        // 暂时返回 0
        return 0;
    }
    
    public String getIntroduction() {
        return description; // 直接返回 description 字段
    }
    
    public String getAdminName() {
        // 这里可以返回话题管理员的用户名
        // 暂时返回空字符串
        return "";
    }
}
