package com.weread.repository;

import com.weread.entity.ReadingProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgressEntity, Integer> {

    // 1. 查询用户某本书的阅读进度
    Optional<ReadingProgressEntity> findByUserIdAndBookId(Integer userId, Integer bookId);

    // 2. 更新阅读进度（章节、页码、进度、最后阅读时间）
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
}