package com.weread.service.community;

import com.weread.entity.community.TopicEntity;

import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface TopicService {
    
    /**
     * 创建一个新话题
     */
    TopicEntity createTopic(String name, String description);

    /**
     * 查找或创建话题列表
     * @param names 话题名称列表
     * @return 存在的或新创建的 TopicEntity 列表
     */
    List<TopicEntity> getOrCreateTopics(Set<String> names);

    /**
     * 获取所有话题
     */
    List<TopicEntity> getAllTopics();
    
    /**
     * 获取单个话题
     */
    Optional<TopicEntity> getTopicById(Long topicId);
}