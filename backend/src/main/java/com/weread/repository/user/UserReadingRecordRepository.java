package com.weread.repository.user;

import com.weread.entity.user.UserReadingRecordEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserReadingRecordRepository extends JpaRepository<UserReadingRecordEntity, Long> {
    
    /**
     * 统计用户某日的阅读时长
     */
    @Query("SELECT SUM(r.readingTime) FROM UserReadingRecordEntity r WHERE r.userId = :userId AND r.readDate = :date")
    Integer sumReadingTimeByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);
    
    /**
     * 统计用户日期范围内的阅读时长
     */
    @Query("SELECT SUM(r.readingTime) FROM UserReadingRecordEntity r WHERE r.userId = :userId AND r.readDate BETWEEN :startDate AND :endDate")
    Integer sumReadingTimeByUserIdAndDateRange(@Param("userId") Integer userId, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * 按日统计阅读时长
     */
    @Query("SELECT r.readDate, SUM(r.readingTime) FROM UserReadingRecordEntity r " +
           "WHERE r.userId = :userId AND r.readDate BETWEEN :startDate AND :endDate " +
           "GROUP BY r.readDate ORDER BY r.readDate")
    List<Object[]> findDailyStatsByUserIdAndDateRange(@Param("userId") Integer userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    /**
     * 按月统计阅读时长
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', r.readDate, '%Y-%m'), SUM(r.readingTime) FROM UserReadingRecordEntity r " +
           "WHERE r.userId = :userId AND r.readDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE_FORMAT', r.readDate, '%Y-%m') " +
           "ORDER BY FUNCTION('DATE_FORMAT', r.readDate, '%Y-%m')")
    List<Object[]> findMonthlyStatsByUserIdAndDateRange(@Param("userId") Integer userId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 统计用户阅读过的不同书籍数量
     */
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM UserReadingRecordEntity r WHERE r.userId = :userId AND r.bookId IS NOT NULL")
    Integer countDistinctBooksByUserId(@Param("userId") Integer userId);

    /**
     * 查找用户在指定日期范围内阅读时间最长的书籍
     * 返回: [bookId, bookTitle, totalReadingTime]
     */
    @Query("SELECT r.bookId, r.bookTitle, SUM(r.readingTime) as totalReadingTime " +
           "FROM UserReadingRecordEntity r " +
           "WHERE r.userId = :userId " +
           "AND r.readDate BETWEEN :startDate AND :endDate " +
           "AND r.bookId IS NOT NULL " +
           "GROUP BY r.bookId, r.bookTitle " +
           "ORDER BY totalReadingTime DESC")
    List<Object[]> findTopBooksByUserIdAndPeriod(@Param("userId") Integer userId, 
                                                  @Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate, 
                                                  Pageable pageable);

    /**
     * 统计用户已读完的书籍数量（通过record_type判断）
     */
    @Query("SELECT COUNT(DISTINCT r.bookId) FROM UserReadingRecordEntity r WHERE r.userId = :userId AND r.recordType = 2")
    Integer countFinishedBooksByUserId(@Param("userId") Integer userId);

    /**
     * 按年统计阅读时长
     */
    @Query("SELECT YEAR(r.readDate), SUM(r.readingTime) " +
           "FROM UserReadingRecordEntity r " +
           "WHERE r.userId = :userId " +
           "GROUP BY YEAR(r.readDate) " +
           "ORDER BY YEAR(r.readDate) DESC")
    List<Object[]> findYearlyStatsByUserId(@Param("userId") Integer userId);

    /**
     * 获取用户最近的阅读记录
     */
    List<UserReadingRecordEntity> findByUserIdOrderByReadDateDesc(@Param("userId") Integer userId, Pageable pageable);

    /**
     * 获取用户已完成的书籍记录
     */
    List<UserReadingRecordEntity> findByUserIdAndRecordTypeOrderByReadDateDesc(@Param("userId") Integer userId, 
                                                                              @Param("recordType") Integer recordType, 
                                                                              Pageable pageable);
}