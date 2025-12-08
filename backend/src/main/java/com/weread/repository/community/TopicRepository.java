package com.weread.repository.community;

import com.weread.entity.community.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 话题仓库
public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    Optional<TopicEntity> findByName(String name);
    List<TopicEntity> findByNameIn(List<String> names);
}
