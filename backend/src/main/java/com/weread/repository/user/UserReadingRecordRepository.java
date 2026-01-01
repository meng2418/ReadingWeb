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

    List<Object[]> findTopBooksByUserIdAndPeriod(Integer userId, LocalDate startDate, LocalDate endDate, int i);

    Integer countFinishedBooksByUserId(Integer userId);
}

