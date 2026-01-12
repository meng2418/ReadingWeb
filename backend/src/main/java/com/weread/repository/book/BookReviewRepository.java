package com.weread.repository.book;

import com.weread.entity.book.BookReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     * 对于同一用户对同一本书的多条书评，只返回最新的一条
     */
    @Query("SELECT r FROM BookReviewEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.status = 1 " +
           "AND r.isPublic = true " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.bookId = r.bookId " +
           "                  AND r2.userId = r.userId " +
           "                  AND r2.status = 1 " +
           "                  AND r2.isPublic = true) " +
           "ORDER BY r.createdAt DESC")
    Page<BookReviewEntity> findByBookId(@Param("bookId") Integer bookId, Pageable pageable);

    /**
     * 根据书籍ID和用户ID查询书评（用于检查用户是否已评价）
     * 如果用户有多条书评，返回最新的一条（reviewId最大的）
     */
    @Query("SELECT r FROM BookReviewEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.userId = :userId " +
           "AND r.status = :status " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.bookId = :bookId " +
           "                  AND r2.userId = :userId " +
           "                  AND r2.status = :status)")
    Optional<BookReviewEntity> findByBookIdAndUserIdAndStatus(@Param("bookId") Integer bookId, 
                                                              @Param("userId") Integer userId, 
                                                              @Param("status") Integer status);

    /**
     * 根据用户ID查询书评列表
     * 对于同一本书，只返回最新的一条书评（reviewId最大的）
     */
    @Query("SELECT r FROM BookReviewEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.status = 1 " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.userId = r.userId " +
           "                  AND r2.bookId = r.bookId " +
           "                  AND r2.status = 1) " +
           "ORDER BY r.createdAt DESC")
    Page<BookReviewEntity> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    /**
     * 统计书籍的评分数量
     * 对于同一用户对同一本书的多条书评，只统计最新的一条
     */
    @Query("SELECT r.rating, COUNT(r) FROM BookReviewEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.status = 1 " +
           "AND r.isPublic = true " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.bookId = r.bookId " +
           "                  AND r2.userId = r.userId " +
           "                  AND r2.status = 1 " +
           "                  AND r2.isPublic = true) " +
           "GROUP BY r.rating")
    List<Object[]> countRatingsByBookId(@Param("bookId") Integer bookId);

    /**
     * 统计书籍的总评论数
     * 对于同一用户对同一本书的多条书评，只统计最新的一条
     */
    @Query("SELECT COUNT(r) FROM BookReviewEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.status = 1 " +
           "AND r.isPublic = true " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.bookId = r.bookId " +
           "                  AND r2.userId = r.userId " +
           "                  AND r2.status = 1 " +
           "                  AND r2.isPublic = true)")
    Long countByBookId(@Param("bookId") Integer bookId);

    /**
     * 游标分页查询用户书评（第一页）
     * 对于同一本书，只返回最新的一条书评（reviewId最大的）
     * 按创建时间倒序，reviewId 倒序
     */
    @Query("SELECT r FROM BookReviewEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.status = 1 " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.userId = r.userId " +
           "                  AND r2.bookId = r.bookId " +
           "                  AND r2.status = 1) " +
           "ORDER BY r.createdAt DESC, r.reviewId DESC")
    List<BookReviewEntity> findUserReviewsFirstPage(@Param("userId") Integer userId, PageRequest pageable);

    /**
     * 游标分页查询用户书评（后续页）
     * 对于同一本书，只返回最新的一条书评（reviewId最大的）
     * cursor 是 reviewId，查询创建时间小于等于指定reviewId的书评
     */
    @Query("SELECT r FROM BookReviewEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.status = 1 " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.userId = r.userId " +
           "                  AND r2.bookId = r.bookId " +
           "                  AND r2.status = 1) " +
           "AND (r.createdAt < (SELECT r2.createdAt FROM BookReviewEntity r2 WHERE r2.reviewId = :cursor) " +
           "OR (r.createdAt = (SELECT r2.createdAt FROM BookReviewEntity r2 WHERE r2.reviewId = :cursor) " +
           "AND r.reviewId < :cursor)) " +
           "ORDER BY r.createdAt DESC, r.reviewId DESC")
    List<BookReviewEntity> findUserReviewsByCursor(@Param("userId") Integer userId, 
                                                    @Param("cursor") Integer cursor, 
                                                    PageRequest pageable);

    /**
     * 统计用户总书评数
     * 对于同一本书，只统计最新的一条书评（表示用户评价了多少本不同的书）
     */
    @Query("SELECT COUNT(r) FROM BookReviewEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.status = 1 " +
           "AND r.reviewId = (SELECT MAX(r2.reviewId) FROM BookReviewEntity r2 " +
           "                  WHERE r2.userId = r.userId " +
           "                  AND r2.bookId = r.bookId " +
           "                  AND r2.status = 1)")
    Long countByUserId(@Param("userId") Integer userId);
}

