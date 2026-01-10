package com.weread.repository;

import com.weread.entity.ReadingProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // ============= 新增统计方法 =============
    
    // 3. 统计用户总阅读书籍数量（有阅读记录的书籍）
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r WHERE r.userId = :userId")
    Integer countTotalBooksRead(@Param("userId") Integer userId);
    
    // 4. 统计用户读完的书籍数量（progress >= 0.99 视为读完）
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId AND r.progress >= 0.99")
    Integer countBooksFinished(@Param("userId") Integer userId);
    
    // 5. 统计指定时间段内更新的书籍数量（表示这段时间在阅读的书籍）
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId AND r.lastReadAt >= :startDate AND r.lastReadAt <= :endDate")
    Integer countBooksReadByPeriod(@Param("userId") Integer userId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    // 6. 统计指定时间段内读完的书籍数量
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM ReadingProgressEntity r " +
           "WHERE r.userId = :userId AND r.progress >= 0.99 " +
           "AND r.lastReadAt >= :startDate AND r.lastReadAt <= :endDate")
    Integer countBooksFinishedByPeriod(@Param("userId") Integer userId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    // 7. 获取用户最近阅读的日期
    @Query("SELECT MAX(r.lastReadAt) FROM ReadingProgressEntity r WHERE r.userId = :userId")
    Optional<LocalDateTime> getLastReadingDate(@Param("userId") Integer userId);
    
    // 8. 获取用户所有阅读记录（用于计算连续天数）- 修复版本
    @Query(value = "SELECT DISTINCT DATE(r.last_read_at) as reading_date " +
                   "FROM readingprogress_info r " +
                   "WHERE r.user_id = :userId " +
                   "ORDER BY reading_date DESC", 
           nativeQuery = true)
    List<Date> getReadingDates(@Param("userId") Integer userId);
}