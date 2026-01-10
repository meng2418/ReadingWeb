package com.weread.repository;

import com.weread.entity.BookshelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Bookshelf Data Access Interface.
 */
public interface BookshelfRepository extends JpaRepository<BookshelfEntity, Integer> {

    // 1. Check if a book exists on the user's bookshelf
    Optional<BookshelfEntity> findByUserIdAndBookId(Integer userId, Integer bookId);

    // 2. Query all bookshelf items for a specific user
    List<BookshelfEntity> findByUserId(Integer userId);

    // 3. Query bookshelf items filtered by status (unread/reading/finished)
    List<BookshelfEntity> findByUserIdAndStatus(Integer userId, String status);

    // 4. Custom query to update a book's status and last read time
    @Modifying
    @Query("UPDATE BookshelfEntity b SET b.status = :status, b.lastReadAt = :lastReadAt " +
            "WHERE b.userId = :userId AND b.bookId = :bookId")
    void updateBookStatus(
            @Param("userId") Integer userId,
            @Param("bookId") Integer bookId,
            @Param("status") String status,
            @Param("lastReadAt") LocalDateTime lastReadAt);
}