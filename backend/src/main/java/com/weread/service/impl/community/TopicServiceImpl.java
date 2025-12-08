package com.weread.service.impl.community;

import com.weread.entity.community.TopicEntity;
import com.weread.repository.community.TopicRepository;
import com.weread.service.community.TopicService;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public TopicEntity createTopic(String name, String description) {
        if (topicRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("话题名称已存在: " + name);
        }
        TopicEntity topic = new TopicEntity();
        topic.setName(name);
        topic.setDescription(description);
        return topicRepository.save(topic);
    }
    
    @Override
    @Transactional
    public List<TopicEntity> getOrCreateTopics(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }
        
        // 1. 查找已存在的话题
        List<TopicEntity> existingTopics = topicRepository.findByNameIn(names.stream().toList());
        
        // 映射已存在的话题名称
        Set<String> existingNames = existingTopics.stream()
                .map(TopicEntity::getName)
                .collect(Collectors.toSet());
        
        // 2. 识别需要新建的话题
        List<TopicEntity> newTopics = names.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> {
                    TopicEntity newTopic = new TopicEntity();
                    newTopic.setName(name);
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
    public Optional<TopicEntity> getTopicById(Long topicId) {
        return topicRepository.findById(topicId);
    }
}