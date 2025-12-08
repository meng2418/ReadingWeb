package com.weread.repository.book;

import com.weread.entity.book.UserBookAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookAccessRepository extends JpaRepository<UserBookAccessEntity, Long> {
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}