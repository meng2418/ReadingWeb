package com.weread.repository.community;

import com.weread.entity.community.TopicFollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TopicFollowRepository extends JpaRepository<TopicFollowEntity, Integer> {
    
    /**
     * 检查用户是否关注了话题
     */
    Boolean existsByUserIdAndTopicId(Integer userId, Integer topicId);
    
    /**
     * 获取用户关注的所有话题记录
     */
    List<TopicFollowEntity> findByUserId(Integer userId);
    
    /**
     * 获取关注某个话题的所有用户记录
     */
    List<TopicFollowEntity> findByTopicId(Integer topicId);
    
    /**
     * 取消关注（删除记录）
     */
    @Modifying
    @Transactional
    void deleteByUserIdAndTopicId(Integer userId, Integer topicId);
    
    /**
     * 统计用户关注的话题数量
     */
    Integer countByUserId(Integer userId);
    
    /**
     * 统计话题的关注人数
     */
    Integer countByTopicId(Integer topicId);
    
    /**
     * 获取用户关注的话题ID列表
     */
    @Query("SELECT tf.topicId FROM TopicFollowEntity tf WHERE tf.userId = :userId")
    List<Integer> findFollowedTopicIdsByUserId(@Param("userId") Integer userId);
    
    /**
     * 批量查询用户关注的话题ID（从指定列表中）
     */
    @Query("SELECT tf.topicId FROM TopicFollowEntity tf WHERE tf.userId = :userId AND tf.topicId IN :topicIds")
    List<Integer> findFollowedTopicIdsByUserIdAndTopicIds(
            @Param("userId") Integer userId, 
            @Param("topicIds") List<Integer> topicIds);
    
    /**
     * 检查用户是否关注了任意一个话题（用于批量判断）
     */
    @Query("SELECT COUNT(tf) > 0 FROM TopicFollowEntity tf WHERE tf.userId = :userId AND tf.topicId IN :topicIds")
    Boolean existsByUserIdAndTopicIdIn(@Param("userId") Integer userId, @Param("topicIds") List<Integer> topicIds);
}
