package com.weread.service.community;

import java.util.List;
import java.util.Map;

public interface TopicFollowService {
    
    /**
     * 关注话题
     * @param userId 用户ID
     * @param topicId 话题ID
     */
    void followTopic(Integer userId, Integer topicId);
    
    /**
     * 取消关注话题
     * @param userId 用户ID
     * @param topicId 话题ID
     */
    void unfollowTopic(Integer userId, Integer topicId);
    
    /**
     * 检查是否关注了话题
     * @param userId 用户ID
     * @param topicId 话题ID
     * @return 是否关注
     */
    boolean isFollowingTopic(Integer userId, Integer topicId);
    
    /**
     * 批量检查用户是否关注多个话题
     * @param userId 用户ID
     * @param topicIds 话题ID列表
     * @return 话题ID -> 是否关注的映射
     */
    Map<Integer, Boolean> batchCheckFollowing(Integer userId, List<Integer> topicIds);
    
    /**
     * 统计话题的关注人数
     * @param topicId 话题ID
     * @return 关注人数
     */
    Integer countTopicFollowers(Integer topicId);
    
    /**
     * 统计用户关注的话题数量
     * @param userId 用户ID
     * @return 关注的话题数量
     */
    Integer countUserFollowedTopics(Integer userId);
    
    /**
     * 获取用户关注的话题ID列表
     * @param userId 用户ID
     * @return 话题ID列表
     */
    List<Integer> getUserFollowedTopicIds(Integer userId);
}