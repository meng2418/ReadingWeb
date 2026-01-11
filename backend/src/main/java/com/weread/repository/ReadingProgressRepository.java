package com.weread.repository;

import com.weread.entity.ReadingProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

/**
 * ReadingProgress Data Access Interface.
 */
public interface ReadingProgressRepository extends JpaRepository<ReadingProgressEntity, Integer> {

    // 1. Query the reading progress of a specific book for a user
    Optional<ReadingProgressEntity> findByUserIdAndBookId(Integer userId, Integer bookId);

    // 2. Update the reading progress (chapterId, currentPage, progress, lastReadAt)
    @Modifying
    @Query("UPDATE ReadingProgressEntity r SET " +
            "r.chapterId = :chapterId, " +
            "r.currentPage = :currentPage, " +
            "r.progress = :progress, " +
            "r.lastReadAt = :lastReadAt " +
            "WHERE r.userId = :userId AND r.bookId = :bookId")
    void updateProgress(
            @Param("userId") Integer userId,
            @Param("bookId") Integer bookId,
            @Param("chapterId") Integer chapterId,
            @Param("currentPage") Integer currentPage,
            @Param("progress") Float progress,
            @Param("lastReadAt") LocalDateTime lastReadAt);

    /**
     * 统计在读人数（progress > 0 且 < 1）
     */
    @Query("SELECT COUNT(r) FROM ReadingProgressEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.progress > 0 AND r.progress < 1")
    Long countReadingUsers(@Param("bookId") Integer bookId);

    /**
     * 统计读完人数（progress = 1）
     */
    @Query("SELECT COUNT(r) FROM ReadingProgressEntity r " +
           "WHERE r.bookId = :bookId " +
           "AND r.progress >= 1")
    Long countFinishedUsers(@Param("bookId") Integer bookId);

    /**
     * 统计用户阅读过的书籍总数（progress > 0）
     */
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId AND r.progress > 0")
    Integer countTotalBooksRead(@Param("userId") Integer userId);

    /**
     * 统计用户已读完的书籍数量（progress = 1）
     */
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId AND r.progress >= 1")
    Integer countBooksFinished(@Param("userId") Integer userId);

    /**
     * 统计用户在指定时间段内阅读过的书籍数量
     */
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.progress > 0 " +
           "AND r.lastReadAt BETWEEN :start AND :end")
    Integer countBooksReadByPeriod(@Param("userId") Integer userId, 
                                   @Param("start") LocalDateTime start, 
                                   @Param("end") LocalDateTime end);

    /**
     * 统计用户在指定时间段内读完的书籍数量
     */
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.progress >= 1 " +
           "AND r.lastReadAt BETWEEN :start AND :end")
    Integer countBooksFinishedByPeriod(@Param("userId") Integer userId, 
                                       @Param("start") LocalDateTime start, 
                                       @Param("end") LocalDateTime end);

    /**
     * 获取用户在指定时间段内阅读的书籍列表
     */
    @Query("SELECT DISTINCT r.bookId FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.lastReadAt BETWEEN :start AND :end " +
           "ORDER BY r.lastReadAt DESC")
    List<Integer> findBookIdsReadByPeriod(@Param("userId") Integer userId, 
                                         @Param("start") LocalDateTime start, 
                                         @Param("end") LocalDateTime end);
}