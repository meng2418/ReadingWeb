package com.weread.repository.user;

import com.weread.entity.user.UserReadingRecordEntity;
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
       @Query("SELECT DATE_FORMAT(r.readDate, '%Y-%m'), SUM(r.readingTime) FROM UserReadingRecordEntity r " +
                     "WHERE r.userId = :userId AND r.readDate BETWEEN :startDate AND :endDate " +
                     "GROUP BY DATE_FORMAT(r.readDate, '%Y-%m') ORDER BY DATE_FORMAT(r.readDate, '%Y-%m')")
       List<Object[]> findMonthlyStatsByUserIdAndDateRange(@Param("userId") Integer userId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       Integer countDistinctBooksByUserId(Integer userId);

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
                     org.springframework.data.domain.Pageable pageable);

       Integer countFinishedBooksByUserId(Integer userId);

       // 首页代码需要的阅读时长统计方法
       // 查询指定用户在本周的阅读时长（只统计阅读记录类型）
       @Query("SELECT COALESCE(SUM(r.readingTime), 0) FROM UserReadingRecordEntity r " +
                     "WHERE r.userId = :userId " +
                     "AND r.recordType = 1 " + // 1-阅读记录
                     "AND r.readDate >= :startOfWeek " +
                     "AND r.readDate <= :endOfWeek")
       Integer findWeeklyReadingTime(@Param("userId") Long userId,
                     @Param("startOfWeek") LocalDate startOfWeek,
                     @Param("endOfWeek") LocalDate endOfWeek);

       // 查询指定用户在本月的阅读时长
       @Query("SELECT COALESCE(SUM(r.readingTime), 0) FROM UserReadingRecordEntity r " +
                     "WHERE r.userId = :userId " +
                     "AND r.recordType = 1 " +
                     "AND r.readDate >= :startOfMonth " +
                     "AND r.readDate <= :endOfMonth")
       Integer findMonthlyReadingTime(@Param("userId") Long userId,
                     @Param("startOfMonth") LocalDate startOfMonth,
                     @Param("endOfMonth") LocalDate endOfMonth);

       // 查询指定用户在本年的阅读时长
       @Query("SELECT COALESCE(SUM(r.readingTime), 0) FROM UserReadingRecordEntity r " +
                     "WHERE r.userId = :userId " +
                     "AND r.recordType = 1 " +
                     "AND r.readDate >= :startOfYear " +
                     "AND r.readDate <= :endOfYear")
       Integer findYearlyReadingTime(@Param("userId") Long userId,
                     @Param("startOfYear") LocalDate startOfYear,
                     @Param("endOfYear") LocalDate endOfYear);

       // 查询指定用户的总阅读时长
       @Query("SELECT COALESCE(SUM(r.readingTime), 0) FROM UserReadingRecordEntity r " +
                     "WHERE r.userId = :userId " +
                     "AND r.recordType = 1")
       Integer findTotalReadingTime(@Param("userId") Long userId);

       // 可选：查询最近一周每天的阅读时长（可用于后续扩展）
       @Query("SELECT r.readDate, COALESCE(SUM(r.readingTime), 0) FROM UserReadingRecordEntity r " +
                     "WHERE r.userId = :userId " +
                     "AND r.recordType = 1 " +
                     "AND r.readDate >= :startDate " +
                     "GROUP BY r.readDate " +
                     "ORDER BY r.readDate")
       List<Object[]> findDailyReadingTimeByDateRange(@Param("userId") Long userId,
                     @Param("startDate") LocalDate startDate);

       // 最近阅读书籍的方法
       // 方案1：分组查询
       @Query("SELECT r.bookId, MAX(r.readDate) as maxReadDate, MAX(r.updatedAt) as maxUpdatedAt " +
                     "FROM UserReadingRecordEntity r " +
                     "WHERE r.userId = :userId " +
                     "AND r.bookId IS NOT NULL " +
                     "AND r.recordType = 1 " +
                     "GROUP BY r.bookId " +
                     "ORDER BY MAX(r.readDate) DESC, MAX(r.updatedAt) DESC")
       List<Object[]> findRecentBookIdsWithMaxDate(@Param("userId") Long userId);

}
