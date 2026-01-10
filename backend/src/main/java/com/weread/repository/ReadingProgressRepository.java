package com.weread.repository;

import com.weread.entity.ReadingProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    Integer countTotalBooksRead(Integer userId);

    Integer countBooksFinished(Integer userId);

    Integer countBooksReadByPeriod(Integer userId, LocalDateTime start, LocalDateTime end);

    Integer countBooksFinishedByPeriod(Integer userId, LocalDateTime start, LocalDateTime end);
}