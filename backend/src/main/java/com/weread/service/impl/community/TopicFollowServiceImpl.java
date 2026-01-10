package com.weread.service.impl.community;

import com.weread.entity.community.TopicEntity;
import com.weread.entity.community.TopicFollowEntity;
import com.weread.exception.BusinessException;
import com.weread.repository.community.TopicRepository;
import com.weread.repository.community.TopicFollowRepository;
import com.weread.service.community.TopicFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicFollowServiceImpl implements TopicFollowService {

    private final TopicFollowRepository topicFollowRepository;
    private final TopicRepository topicRepository;

    @Override
    @Transactional
    public void followTopic(Integer userId, Integer topicId) {

        // 1. 验证话题是否存在
        TopicEntity topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("用户 {} 尝试关注不存在的话题 {}", userId, topicId);
                    return new BusinessException("话题不存在");
                });
        
        // 2. 检查是否已关注
        if (topicFollowRepository.existsByUserIdAndTopicId(userId, topicId)) {
            log.warn("用户 {} 已经关注了话题 {}", userId, topicId);
            throw new BusinessException("您已经关注了该话题");
        }
        
        // 3. 创建关注记录
        TopicFollowEntity follow = new TopicFollowEntity();
        follow.setUserId(userId);
        follow.setTopicId(topicId);
        follow.setCreatedAt(LocalDateTime.now());
        
        topicFollowRepository.save(follow);
        
        // 4. 更新话题的关注人数
        int currentCount = topic.getFollowerCount() != null ? topic.getFollowerCount() : 0;
        topic.setFollowerCount(currentCount + 1);
        topicRepository.save(topic);
        
        log.info("用户 {} 成功关注话题 {}，当前关注人数：{}", 
                userId, topicId, topic.getFollowerCount());
    }

    @Override
    @Transactional
    public void unfollowTopic(Integer userId, Integer topicId) {
        // 1. 验证话题是否存在
        TopicEntity topic = topicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.warn("用户 {} 尝试取消关注不存在的话题 {}", userId, topicId);
                    return new BusinessException("话题不存在");
                });
        
        // 2. 检查是否已关注
        if (!topicFollowRepository.existsByUserIdAndTopicId(userId, topicId)) {
            log.warn("用户 {} 尝试取消关注未关注的话题 {}", userId, topicId);
            throw new BusinessException("您还未关注该话题");
        }
        
        // 3. 删除关注记录
        topicFollowRepository.deleteByUserIdAndTopicId(userId, topicId);
        
        // 4. 更新话题的关注人数
        int currentCount = topic.getFollowerCount() != null ? topic.getFollowerCount() : 0;
        int newCount = Math.max(0, currentCount - 1);
        topic.setFollowerCount(newCount);
        topicRepository.save(topic);
        
        log.info("用户 {} 成功取消关注话题 {}，当前关注人数：{}", 
                userId, topicId, topic.getFollowerCount());
    }

    @Override
    public boolean isFollowingTopic(Integer userId, Integer topicId) {
        if (userId == null) {
            return false;
        }
        return topicFollowRepository.existsByUserIdAndTopicId(userId, topicId);
    }

    @Override
    public Map<Integer, Boolean> batchCheckFollowing(Integer userId, List<Integer> topicIds) {
        if (userId == null || topicIds == null || topicIds.isEmpty()) {
            return topicIds.stream()
                    .collect(Collectors.toMap(id -> id, id -> false));
        }
        
        // 查询用户已关注的话题ID列表
        List<Integer> followedIds = topicFollowRepository
                .findFollowedTopicIdsByUserIdAndTopicIds(userId, topicIds);
        
        // 构建结果映射
        return topicIds.stream()
                .collect(Collectors.toMap(
                        topicId -> topicId,
                        topicId -> followedIds.contains(topicId)
                ));
    }

    @Override
    public Integer countTopicFollowers(Integer topicId) {
        Integer count = topicFollowRepository.countByTopicId(topicId);
        return count != null ? count : 0;
    }

    @Override
    public Integer countUserFollowedTopics(Integer userId) {
        Integer count = topicFollowRepository.countByUserId(userId);
        return count != null ? count : 0;
    }

    @Override
    public List<Integer> getUserFollowedTopicIds(Integer userId) {
        return topicFollowRepository.findFollowedTopicIdsByUserId(userId);
    }
}