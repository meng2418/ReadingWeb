package com.weread.service.community;

import com.weread.entity.community.TopicEntity;
import com.weread.vo.community.HotTopicVO;
import com.weread.vo.community.TopicDetailVO;
import com.weread.dto.response.community.TopicListResponse;
import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface TopicService {
    
    /**
     * 创建一个新话题
     */
    TopicEntity createTopic(String topicName, String description);

    /**
     * 查找或创建话题列表
     * @param topicNames 话题名称列表
     * @return 存在的或新创建的 TopicEntity 列表
     */
    List<TopicEntity> getOrCreateTopics(Set<String> topicNames);

    /**
     * 获取所有话题
     */
    List<TopicEntity> getAllTopics();
    
    /**
     * 获取单个话题
     */
    Optional<TopicEntity> getTopicById(Integer topicId);

    // 新增方法：获取话题瀑布流列表
    TopicListResponse getTopics(Integer cursor, Integer limit);

    /**
    * 获取热门话题（最多10个）
    */
    List<HotTopicVO> getHotTopics();

    /**
    * 获取话题详情
    */
    TopicDetailVO getTopicDetail(Integer topicId, Integer currentUserId);
}