package com.weread.repository.community;

import com.weread.entity.community.TopicEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// 话题仓库
public interface TopicRepository extends JpaRepository<TopicEntity, Integer> {
    Optional<TopicEntity> findByTopicName(String topicName);
    List<TopicEntity> findByTopicNameIn(List<String> topicNames);

    /**
     * 瀑布流分页查询
     * @param cursor 游标（最后一个topicId）
     * @param pageable 分页信息
     * @return 话题列表
     */
    @Query("SELECT t FROM TopicEntity t " +
           "WHERE (:cursor IS NULL OR t.topicId > :cursor) " +
           "ORDER BY t.topicId ASC")
    List<TopicEntity> findTopicsByCursor(
            @Param("cursor") Integer cursor,
            PageRequest pageable);
    
    /**
     * 检查指定游标后是否还有更多数据
     * @param cursor 游标
     * @return 是否还有更多数据
     */
    @Query("SELECT COUNT(t) > 0 FROM TopicEntity t " +
           "WHERE t.topicId > :cursor")
    boolean existsMoreAfterCursor(@Param("cursor") Integer cursor);

    @Query("SELECT t FROM TopicEntity t " +
        "ORDER BY t.postCount DESC, t.topicId DESC")
    List<TopicEntity> findHotTopics(PageRequest pageable);
    
    /**
     * 查找相关话题（排除当前话题，按热度排序）
     */
    @Query("SELECT t FROM TopicEntity t " +
           "WHERE t.topicId != :topicId " +
           "ORDER BY t.postCount DESC, t.topicId DESC")
    List<TopicEntity> findRelatedTopics(@Param("topicId") Integer topicId, Pageable pageable);
    
    /**
     * 根据帖子ID查找相关话题（排除当前话题）
     * 通过 PostTopicEntity 中间表查找与当前帖子相关的其他话题
     */
    @Query("SELECT DISTINCT t FROM TopicEntity t " +
           "INNER JOIN t.postTopics pt1 ON t.topicId = pt1.topicId " +
           "WHERE pt1.postId = :postId " +
           "AND t.topicId != :currentTopicId " +
           "ORDER BY t.postCount DESC, t.topicId DESC")
    List<TopicEntity> findRelatedTopicsByPostId(@Param("postId") Integer postId, 
                                                 @Param("currentTopicId") Integer currentTopicId, 
                                                 Pageable pageable);

    /**
     * 搜索话题（按名称）
     */
    @Query("SELECT t FROM TopicEntity t " +
           "WHERE t.topicName LIKE %:keyword% " +
           "ORDER BY t.postCount DESC, t.createdAt DESC")
    Page<TopicEntity> searchTopics(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
}
