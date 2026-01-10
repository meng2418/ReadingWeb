package com.weread.service.impl.community;

import com.weread.dto.response.community.TopicListResponse;
import com.weread.entity.community.TopicEntity;
import com.weread.repository.community.TopicRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.community.TopicFollowRepository;
import com.weread.service.community.TopicService;
import com.weread.vo.community.HotTopicVO;
import com.weread.vo.community.TopicDetailRelatedVO;
import com.weread.vo.community.TopicDetailVO;
import com.weread.vo.community.TopicVO;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final PostRepository postRepository;
    private final TopicFollowRepository topicFollowRepository;

    @Override
    @Transactional
    public TopicEntity createTopic(String topicName, String description) {
        if (topicRepository.findByTopicName(topicName).isPresent()) {
            throw new IllegalArgumentException("话题名称已存在: " + topicName);
        }
        TopicEntity topic = new TopicEntity();
        topic.setTopicName(topicName);
        topic.setDescription(description);
        return topicRepository.save(topic);
    }
    
    @Override
    @Transactional
    public List<TopicEntity> getOrCreateTopics(Set<String> topicNames) {
        if (topicNames == null || topicNames.isEmpty()) {
            return List.of();
        }
        
        // 1. 查找已存在的话题
        List<TopicEntity> existingTopics = topicRepository.findByTopicNameIn(topicNames.stream().toList());
        
        // 映射已存在的话题名称
        Set<String> existingNames = existingTopics.stream()
                .map(TopicEntity::getTopicName)
                .collect(Collectors.toSet());
        
        // 2. 识别需要新建的话题
        List<TopicEntity> newTopics = topicNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> {
                    TopicEntity newTopic = new TopicEntity();
                    newTopic.setTopicName(name);
                    newTopic.setDescription("用户创建的话题");
                    return newTopic;
                })
                .toList();

        // 3. 批量保存新话题
        List<TopicEntity> savedTopics = topicRepository.saveAll(newTopics);

        // 4. 返回所有话题 (已存在 + 新建)
        List<TopicEntity> allTopics = new ArrayList<>(existingTopics);
        allTopics.addAll(savedTopics);
        return allTopics;
    }

    @Override
    public List<TopicEntity> getAllTopics() {
        return topicRepository.findAll();
    }
    
    @Override
    public Optional<TopicEntity> getTopicById(Integer topicId) {
        return topicRepository.findById(topicId);
    }

    // 新增方法实现
    @Override
    @Transactional(readOnly = true)
    public TopicListResponse getTopics(Integer cursor, Integer limit) {
        // 参数验证
        if (limit == null || limit <= 0) limit = 20;
        if (limit > 100) limit = 100;
        
        // 查询数据
        List<TopicEntity> topics = topicRepository.findTopicsByCursor(
            cursor, 
            PageRequest.of(0, limit)
        );
        
        // 转换为VO
        List<TopicVO> items = topics.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        // 计算是否有更多数据
        boolean hasMore = false;
        Integer nextCursor = null;
        
        if (!items.isEmpty()) {
            nextCursor = items.get(items.size() - 1).getTopicId();
            hasMore = topicRepository.existsMoreAfterCursor(nextCursor);
        }
        
        // 构建响应
        TopicListResponse response = new TopicListResponse();
        response.setItems(items);
        response.setHasMore(hasMore);
        response.setNextCursor(nextCursor);
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotTopicVO> getHotTopics() {
        // 查询最多10个热门话题（按帖子数排序）
        List<TopicEntity> topics = topicRepository.findHotTopics(
            PageRequest.of(0, 10)
        );
    
        return topics.stream().map(topic -> {
            HotTopicVO vo = new HotTopicVO();
            vo.setTopicId(topic.getTopicId());
            vo.setTopicName(topic.getTopicName());  // 实体字段是topicName
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDetailVO getTopicDetail(Integer topicId, Integer currentUserId) {
        // 1. 查询话题基本信息
        TopicEntity topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("话题不存在"));
    
        // 2. 查询相关话题（例如：相同分类或热门话题）
        List<TopicEntity> relatedTopics = topicRepository.findRelatedTopics(topicId, 5);
    
        // 3. 查询今日帖子数（需要单独统计）
        Integer todayPostCount = postRepository.countTodayPostsByTopic(topicId);
    
        // 4. 查询是否已关注（使用 TopicFollowRepository）
        Boolean isFollowing = false;
        if (currentUserId != null) {
            // 使用 topicFollowRepository 检查用户是否关注话题
            isFollowing = topicFollowRepository.existsByUserIdAndTopicId(currentUserId, topicId);
        }
    
        // 5. 构建响应
        TopicDetailVO vo = new TopicDetailVO();
        vo.setImage(topic.getImage());
        vo.setTopicName(topic.getTopicName());
        vo.setPostCount(topic.getPostCount());
        vo.setDescription(topic.getDescription());
        vo.setFollowerCount(topic.getFollowerCount());  // 需要实体有该字段
        vo.setTodayPostCount(todayPostCount);
        vo.setIsFollowing(isFollowing);
        vo.setIntroduction(topic.getIntroduction());  // 需要实体有该字段
        vo.setCreatedAt(topic.getCreatedAt());
        vo.setAdminName(topic.getAdminName());  // 需要实体有该字段
    
        // 6. 转换相关话题
        vo.setRelatedTopics(relatedTopics.stream()
            .map(this::convertToTopicDetailRelatedVO)
            .collect(Collectors.toList()));
    
        return vo;
    }

    private TopicVO convertToVO(TopicEntity entity) {
        TopicVO vo = new TopicVO();
        vo.setTopicId(entity.getTopicId());
        vo.setTopicName(entity.getTopicName());
        vo.setImage(entity.getImage());  
        vo.setPostCount(entity.getPostCount());
        return vo;
    }

    private TopicDetailRelatedVO convertToTopicDetailRelatedVO(TopicEntity topic) {
        TopicDetailRelatedVO vo = new TopicDetailRelatedVO();
        vo.setImage(topic.getImage());
        vo.setTopicName(topic.getTopicName());  
        vo.setPostCount(topic.getPostCount());
        vo.setTopicId(topic.getTopicId());
        return vo;
    }
}