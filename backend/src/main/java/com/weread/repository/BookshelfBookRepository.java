package com.weread.repository;

import com.weread.entity.BookshelfBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookshelfBookRepository extends JpaRepository<BookshelfBookEntity, Integer> {

    // 按用户ID和书籍ID查询
    Optional<BookshelfBookEntity> findByUserIdAndBookId(Integer userId, Integer bookId);

    // 按用户ID查询所有书籍（无分页）
    List<BookshelfBookEntity> findByUserId(Integer userId);

    // 按用户ID和状态查询所有书籍（无分页）
    List<BookshelfBookEntity> findByUserIdAndStatus(Integer userId, String status);

    // 更新阅读进度的方法（保持不变）
    @Modifying
    @Query("UPDATE BookshelfBookEntity b SET b.chapterIndex = :chapterIndex, b.pageNum = :pageNum, b.lastReadTime = :lastReadTime WHERE b.userId = :userId AND b.bookId = :bookId")
    void updateReadingProgress(
            @Param("userId") Integer userId,
            @Param("bookId") Integer bookId,
            @Param("chapterIndex") Integer chapterIndex,
            @Param("pageNum") Integer pageNum,
            @Param("lastReadTime") LocalDateTime lastReadTime);
}