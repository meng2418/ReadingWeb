package com.weread.repository.book;

import com.weread.entity.book.BookReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {

    /**
     * 根据书籍ID查询书评列表（公开的，按时间倒序）
     */
    @Query("SELECT r FROM BookReviewEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.status = 1 " +
           "AND r.isPublic = true " +
           "ORDER BY r.createdAt DESC")
    Page<BookReviewEntity> findByBookId(@Param("bookId") Integer bookId, Pageable pageable);

    /**
     * 根据书籍ID和用户ID查询书评（用于检查用户是否已评价）
     */
    Optional<BookReviewEntity> findByBookIdAndUserIdAndStatus(Integer bookId, Integer userId, Integer status);

    /**
     * 根据用户ID查询书评列表
     */
    @Query("SELECT r FROM BookReviewEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.status = 1 " +
           "ORDER BY r.createdAt DESC")
    Page<BookReviewEntity> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    /**
     * 统计书籍的评分数量
     */
    @Query("SELECT r.rating, COUNT(r) FROM BookReviewEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.status = 1 " +
           "AND r.isPublic = true " +
           "GROUP BY r.rating")
    List<Object[]> countRatingsByBookId(@Param("bookId") Integer bookId);

    /**
     * 统计书籍的总评论数
     */
    @Query("SELECT COUNT(r) FROM BookReviewEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.status = 1 " +
           "AND r.isPublic = true")
    Long countByBookId(@Param("bookId") Integer bookId);
}

