package com.weread.entity.community;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "topic_info")
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @Column(unique = true, nullable = false, length = 50)
    private String name; // 话题名称

    @Column(length = 255)
    private String description;

    // 关联到中间表
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTopicEntity> postTopics;
}
