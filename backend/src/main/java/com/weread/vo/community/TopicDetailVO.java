package com.weread.vo.community;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TopicDetailVO {
    private String image;
    private String topicName;
    private Integer postCount;
    private String description;
    private Integer followerCount;
    private Integer todayPostCount;
    private Boolean isFollowing;
    private String introduction;
    private LocalDateTime createdAt;
    private String adminName;
    private List<TopicDetailRelatedVO> relatedTopics;
}