package com.weread.repository.community;

import com.weread.entity.BookEntity;
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

    /**
     * 统计某个话题今天的帖子数
     */
    @Query(value = "SELECT COUNT(DISTINCT p.post_id) FROM post_info p " +
           "INNER JOIN post_topic_info pt ON p.post_id = pt.post_id " +
           "WHERE pt.topic_id = :topicId " +
           "AND DATE(p.created_at) = CURDATE() " +
           "AND p.status = 1", nativeQuery = true)
    Integer countTodayPostsByTopic(@Param("topicId") Integer topicId);

    /**
     * 根据话题ID和排序方式查询帖子（第一页 - latest排序）
     */
    @Query("SELECT DISTINCT p FROM PostEntity p " +
           "INNER JOIN p.postTopics pt " +
           "WHERE pt.topicId = :topicId " +
           "AND p.status = 1 " +
           "ORDER BY p.createdAt DESC")
    List<PostEntity> findPostsByTopicAndSortLatest(@Param("topicId") Integer topicId, Pageable pageable);

    /**
     * 根据话题ID和排序方式查询帖子（第一页 - hot排序）
     */
    @Query("SELECT DISTINCT p FROM PostEntity p " +
           "INNER JOIN p.postTopics pt " +
           "WHERE pt.topicId = :topicId " +
           "AND p.status = 1 " +
           "ORDER BY p.likesCount DESC")
    List<PostEntity> findPostsByTopicAndSortHot(@Param("topicId") Integer topicId, Pageable pageable);

    /**
     * 根据话题ID和排序方式查询帖子（第一页 - quality排序）
     */
    @Query("SELECT DISTINCT p FROM PostEntity p " +
           "INNER JOIN p.postTopics pt " +
           "WHERE pt.topicId = :topicId " +
           "AND p.status = 1 " +
           "ORDER BY (p.likesCount + p.commentsCount) DESC")
    List<PostEntity> findPostsByTopicAndSortQuality(@Param("topicId") Integer topicId, Pageable pageable);

    /**
     * 根据话题ID和排序方式查询帖子（第一页）
     */
    default List<PostEntity> findPostsByTopicAndSort(Integer topicId, String sort, Integer limit) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        return switch (sort) {
            case "hot" -> findPostsByTopicAndSortHot(topicId, pageable);
            case "quality" -> findPostsByTopicAndSortQuality(topicId, pageable);
            default -> findPostsByTopicAndSortLatest(topicId, pageable);
        };
    }

    /**
     * 根据话题ID、排序方式和游标查询帖子（分页 - latest排序）
     */
    @Query("SELECT DISTINCT p FROM PostEntity p " +
           "INNER JOIN p.postTopics pt " +
           "WHERE pt.topicId = :topicId " +
           "AND p.status = 1 " +
           "AND p.postId > :cursor " +
           "ORDER BY p.createdAt DESC")
    List<PostEntity> findPostsByTopicAndCursorLatest(@Param("topicId") Integer topicId, 
                                                      @Param("cursor") Integer cursor, 
                                                      Pageable pageable);

    /**
     * 根据话题ID、排序方式和游标查询帖子（分页 - hot排序）
     */
    @Query("SELECT DISTINCT p FROM PostEntity p " +
           "INNER JOIN p.postTopics pt " +
           "WHERE pt.topicId = :topicId " +
           "AND p.status = 1 " +
           "AND p.postId > :cursor " +
           "ORDER BY p.likesCount DESC")
    List<PostEntity> findPostsByTopicAndCursorHot(@Param("topicId") Integer topicId, 
                                                  @Param("cursor") Integer cursor, 
                                                  Pageable pageable);

    /**
     * 根据话题ID、排序方式和游标查询帖子（分页 - quality排序）
     */
    @Query("SELECT DISTINCT p FROM PostEntity p " +
           "INNER JOIN p.postTopics pt " +
           "WHERE pt.topicId = :topicId " +
           "AND p.status = 1 " +
           "AND p.postId > :cursor " +
           "ORDER BY (p.likesCount + p.commentsCount) DESC")
    List<PostEntity> findPostsByTopicAndCursorQuality(@Param("topicId") Integer topicId, 
                                                       @Param("cursor") Integer cursor, 
                                                       Pageable pageable);

    /**
     * 根据话题ID、排序方式和游标查询帖子（分页）
     */
    default List<PostEntity> findPostsByTopicAndCursor(Integer topicId, String sort, Integer cursor, Integer limit, Pageable pageable) {
        return switch (sort) {
            case "hot" -> findPostsByTopicAndCursorHot(topicId, cursor, pageable);
            case "quality" -> findPostsByTopicAndCursorQuality(topicId, cursor, pageable);
            default -> findPostsByTopicAndCursorLatest(topicId, cursor, pageable);
        };
    }

    Integer countByAuthorId(Integer authorId);

    Page<PostEntity> findByAuthorIdInAndStatus(List<Integer> followingIds, int i, Pageable pageable);

    Page<PostEntity> findByStatusOrderByCreatedAtDesc(int i, Pageable pageable);

    /**
     * 搜索书籍（按标题或作者）
     */
    @Query("SELECT DISTINCT b FROM BookEntity b " +
           "LEFT JOIN b.author a " +
           "WHERE (b.title LIKE %:keyword% OR " +
           "       (a IS NOT NULL AND a.name LIKE %:keyword%)) " +
           "AND b.isPublished = true " +
           "ORDER BY b.readCount DESC, b.createdAt DESC")
    Page<BookEntity> searchBooks(@Param("keyword") String keyword, Pageable pageable);

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
}