package com.weread.repository;

import com.weread.entity.ReadingProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgressEntity, Integer> {

    // 1. ��ѯ�û�ĳ������Ķ�����
    Optional<ReadingProgressEntity> findByUserIdAndBookId(Long userId, Integer bookId);

    // 2. �����Ķ����ȣ��½ڡ�ҳ�롢���ȡ�����Ķ�ʱ�䣩
    @Modifying
    @Query("UPDATE ReadingProgressEntity r SET " +
            "r.chapterId = :chapterId, " +
            "r.currentPage = :currentPage, " +
            "r.progress = :progress, " +
            "r.lastReadAt = :lastReadAt " +
            "WHERE r.userId = :userId AND r.bookId = :bookId")
    void updateProgress(
            @Param("userId") Long userId,
            @Param("bookId") Integer bookId,
            @Param("chapterId") Integer chapterId,
            @Param("currentPage") Integer currentPage,
            @Param("progress") Float progress,
            @Param("lastReadAt") LocalDateTime lastReadAt);
}