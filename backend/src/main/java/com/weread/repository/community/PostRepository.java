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

    Integer countTodayPostsByTopic(Integer topicId);

    List<PostEntity> findPostsByTopicAndSort(Integer topicId, String sort, Integer limit);

    List<PostEntity> findPostsByTopicAndCursor(Integer topicId, String sort, Integer cursor, Integer limit,
            Pageable pageable);

    Integer countByUserId(Integer userId);

    Page<PostEntity> findByAuthorIdInAndStatus(List<Integer> followingIds, int i, Pageable pageable);

    Page<PostEntity> findByStatusOrderByCreatedAtDesc(int i, Pageable pageable);

    /**
     * 搜索书籍（按标题或作者）
     */
    @Query("SELECT DISTINCT b FROM BookEntity b " +
           "LEFT JOIN b.author a " +
           "WHERE (b.title LIKE %:keyword% OR " +
           "       (a IS NOT NULL AND (a.name LIKE %:keyword% OR a.penName LIKE %:keyword%))) " +
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