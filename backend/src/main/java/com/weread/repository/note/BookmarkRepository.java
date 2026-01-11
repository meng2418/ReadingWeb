package com.weread.repository.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weread.entity.note.BookmarkEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {

    /**
     * 查询用户的所有书签
     */
    List<BookmarkEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 查询用户某本书的书签
     */
    List<BookmarkEntity> findByUserIdAndBookIdOrderByCreatedAtDesc(Long userId, Integer bookId);

    /**
     * 查询特定书签
     */
    Optional<BookmarkEntity> findByUserIdAndBookIdAndChapterId(Long userId, Integer bookId, Integer chapterId);

    /**
     * 删除用户的书签
     */
    void deleteByUserIdAndBookmarkId(Long userId, Integer bookmarkId);
}
