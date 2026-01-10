package com.weread.repository.user;

import com.weread.entity.user.UserReadingRecordEntity;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.PageRequest;
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

    @Query("SELECT YEAR(urr.readDate), SUM(urr.readingTime) " +
       "FROM UserReadingRecordEntity urr " +
       "WHERE urr.userId = :userId " +
       "GROUP BY YEAR(urr.readDate) " +
       "ORDER BY YEAR(urr.readDate) DESC")
       List<Object[]> findYearlyStatsByUserId(@Param("userId") Integer userId);

    List<UserReadingRecordEntity> findTopByUserIdOrderByReadDateDesc(Integer userId, PageRequest of);

    List<UserReadingRecordEntity> findFinishedBooksByUserId(Integer userId, PageRequest of);

    @Query("SELECT r.book.id, b.title, SUM(r.readingDuration) " +
       "FROM UserReadingRecordEntity r " +
       "JOIN BookEntity b ON r.book.id = b.id " +
       "WHERE r.user.id = :userId " +
       "AND r.createdAt BETWEEN :startDate AND :endDate " +
       "GROUP BY r.book.id, b.title " +
       "ORDER BY SUM(r.readingDuration) DESC")
List<Object[]> findTopBooksByUserIdAndPeriod(@Param("userId") Integer userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             Pageable pageable);
}

