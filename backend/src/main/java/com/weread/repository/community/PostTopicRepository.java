package com.weread.repository.community;

import com.weread.entity.community.PostTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 帖子话题关联仓库
public interface PostTopicRepository extends JpaRepository<PostTopicEntity, PostTopicEntity.PostTopicId> {
    // 根据帖子ID查找所有关联
    List<PostTopicEntity> findByPostId(Long postId); 
    
    // 根据话题ID查找所有关联
    List<PostTopicEntity> findByTopicId(Long topicId);
}