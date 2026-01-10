package com.weread.repository.note;

import com.weread.entity.note.NoteEntity;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    /**
     * 查询某个用户的所有笔记，按创建时间降序排列
     */
    Page<NoteEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 查询某本书籍的所有公开笔记
     */
    Page<NoteEntity> findByBookIdAndIsPublicTrueOrderByCreatedAtDesc(Integer bookId, Pageable pageable);

    /**
     * 查询某一章节的笔记（包括私有和公开的）
     */
    List<NoteEntity> findByChapterIdOrderByCreatedAtDesc(Integer chapterId);

    Integer countByUserId(Integer userId);

    Integer countByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b.title, n.content, n.createdAt FROM NoteEntity n " +
           "LEFT JOIN BookEntity b ON n.bookId = b.bookId " +
           "WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Object[]> findRecentNoteWithBook(@Param("userId") Integer userId, Pageable pageable);
}