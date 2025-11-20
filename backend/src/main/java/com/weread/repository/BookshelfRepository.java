package com.weread.repository;

import com.weread.entity.BookshelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookshelfRepository extends JpaRepository<BookshelfEntity, Integer> {

    // 1. 检查书籍是否在用户书架中
    Optional<BookshelfEntity> findByUserIdAndBookId(Integer userId, Integer bookId);

    // 2. 查询用户书架中的所有书籍（全部书架）
    List<BookshelfEntity> findByUserId(Integer userId);

    // 3. 按状态筛选用户书架中的书籍（未读/阅读中/已完成）
    List<BookshelfEntity> findByUserIdAndStatus(Integer userId, String status);

    // 4. 更新书籍在书架中的状态（如从reading改为finished）
    @Modifying
    @Query("UPDATE BookshelfEntity b SET b.status = :status, b.lastReadAt = :lastReadAt " +
            "WHERE b.userId = :userId AND b.bookId = :bookId")
    void updateBookStatus(
            @Param("userId") Integer userId,
            @Param("bookId") Integer bookId,
            @Param("status") String status,
            @Param("lastReadAt") LocalDateTime lastReadAt);
}