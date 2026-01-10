package com.weread.repository.community;

import com.weread.entity.community.PostEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// 继承 JpaSpecificationExecutor 用于支持动态查询 (筛选 all, mine, following)
public interface PostRepository extends JpaRepository<PostEntity, Integer>, JpaSpecificationExecutor<PostEntity> {
    
    // 自定义方法：查找作者 ID 在列表中的帖子（用于 following 筛选）
    List<PostEntity> findByAuthorIdIn(List<Integer> authorIds);

    @Query("SELECT COUNT(DISTINCT p) FROM PostEntity p " +
       "JOIN PostTopicEntity pt ON p.postId = pt.postId " +
       "WHERE pt.topicId = :topicId " +
       "AND DATE(p.createdAt) = CURRENT_DATE")
        Integer countTodayPostsByTopic(@Param("topicId") Integer topicId);

    @Query("SELECT DISTINCT p FROM PostEntity p " +
       "JOIN PostTopicEntity pt ON p.postId = pt.postId " +
       "WHERE pt.topicId = :topicId " +
       "ORDER BY " +
       "CASE WHEN :sort = 'hot' THEN p.likesCount END DESC, " +
       "CASE WHEN :sort = 'new' THEN p.createdAt END DESC")
        List<PostEntity> findPostsByTopicAndSort(@Param("topicId") Integer topicId, 
                                                 @Param("sort") String sort, 
                                                 @Param("limit") Integer limit);

    @Query("SELECT DISTINCT p FROM PostEntity p " +
       "JOIN PostTopicEntity pt ON p.postId = pt.postId " +
       "WHERE pt.topicId = :topicId " +
       "AND (:cursorId IS NULL OR p.postId < :cursorId) " +
       "ORDER BY " +
       "CASE WHEN :sort = 'hot' THEN p.likesCount END DESC, " +
       "CASE WHEN :sort = 'new' THEN p.createdAt END DESC")
        List<PostEntity> findPostsByTopicAndCursor(@Param("topicId") Integer topicId,
                                                   @Param("sort") String sort,
                                                   @Param("cursorId") Integer cursorId,
                                                   @Param("limit") Integer limit);

    Page<PostEntity> findByAuthorIdInAndStatus(List<Integer> followingIds, int i, Pageable pageable);

    Page<PostEntity> findByStatusOrderByCreatedAtDesc(int i, Pageable pageable);


    Page<PostEntity> findByAuthorIdInAndStatusOrderByCreatedAtDesc(List<Integer> followingIds, int i,
            Pageable pageable);

    /**
     * 根据作者ID列表和状态查询帖子（用于关注页）
     */
    @Query("SELECT p FROM PostEntity p WHERE p.authorId IN :authorIds AND p.status = :status ORDER BY p.createdAt DESC")
    Page<PostEntity> findByAuthorIdInAndStatusOrderByCreatedAtDesc(
            @Param("authorIds") List<Integer> authorIds,
            @Param("status") Integer status,
            Pageable pageable);
    
    /**
     * 根据状态查询帖子（用于广场）
     */
    Page<PostEntity> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);

    /**
     * 根据作者ID和游标查询帖子（用于瀑布流）
     */
    @Query("SELECT p FROM PostEntity p " +
           "WHERE p.authorId = :authorId " +
           "AND (:cursorId IS NULL OR p.postId < :cursorId) " +
           "AND p.status = 1 " +
           "ORDER BY p.createdAt DESC")
    List<PostEntity> findByAuthorIdAndPostIdLessThanOrderByCreatedAtDesc(
            @Param("authorId") Integer authorId,
            @Param("cursorId") Integer cursorId,
            Pageable pageable);
    
    /**
     * 根据作者ID查询帖子（第一页）
     */
    @Query("SELECT p FROM PostEntity p " +
           "WHERE p.authorId = :authorId " +
           "AND p.status = 1 " +
           "ORDER BY p.createdAt DESC")
    List<PostEntity> findByAuthorIdOrderByCreatedAtDesc(
            @Param("authorId") Integer authorId,
            Pageable pageable);
    
    /**
     * 统计作者ID小于指定帖子ID的帖子数量
     */
    @Query("SELECT COUNT(p) FROM PostEntity p " +
           "WHERE p.authorId = :authorId " +
           "AND p.postId < :postId " +
           "AND p.status = 1")
    Long countByAuthorIdAndPostIdLessThan(
            @Param("authorId") Integer authorId,
            @Param("postId") Integer postId);
    
    /**
     * 统计用户的帖子总数
     */
    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.authorId = :userId AND p.status = 1")
    Integer countByUserId(@Param("userId") Integer userId);
    
    /**
     * 统计用户帖子的总评论数
     */
    @Query("SELECT COALESCE(SUM(p.commentsCount), 0) FROM PostEntity p WHERE p.authorId = :userId AND p.status = 1")
    Integer sumCommentsByUserId(@Param("userId") Integer userId);
    
    /**
     * 统计用户帖子的总点赞数
     */
    @Query("SELECT COALESCE(SUM(p.likesCount), 0) FROM PostEntity p WHERE p.authorId = :userId AND p.status = 1")
    Integer sumLikesByUserId(@Param("userId") Integer userId);

    /**
     * 统计用户帖子
     */
    @Query("SELECT p.postId FROM PostEntity p WHERE p.authorId = :userId")
    List<Integer> findPostIdsByUserId(@Param("userId") Integer userId);

    //我的帖子瀑布流
    @Query("SELECT p FROM PostEntity p WHERE p.authorId = :userId ORDER BY p.createdAt DESC")
    List<PostEntity> findUserPosts(@Param("userId") Integer userId, Pageable pageable);

    //我的帖子瀑布流
    @Query("SELECT p FROM PostEntity p WHERE p.authorId = :userId AND p.postId < :cursorId ORDER BY p.createdAt DESC")
    List<PostEntity> findUserPostsAfterCursor(
        @Param("userId") Integer userId,
        @Param("cursorId") Integer cursorId,
        Pageable pageable);
}