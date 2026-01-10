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
public interface NoteRepository extends JpaRepository<NoteEntity, Integer> {

    /**
     * 查询某个用户的所有笔记，按创建时间降序排列
     */
    Page<NoteEntity> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /**
     * 查询某本书籍的所有公开笔记
     */
    Page<NoteEntity> findByBookIdAndIsPublicTrueOrderByCreatedAtDesc(Integer bookId, Pageable pageable);

    /**
     * 查询某一章节的笔记（包括私有和公开的）
     */
    List<NoteEntity> findByChapterIdOrderByCreatedAtDesc(Integer chapterId);

    /**
     * 查询指定用户、指定书籍和章节的笔记，按创建时间降序排列
     */
    List<NoteEntity> findByUserIdAndBookIdAndChapterIdOrderByCreatedAtDesc(
            Integer userId, Integer bookId, Integer chapterId);

    /**
     * 查询指定用户、指定书籍的所有笔记，按创建时间降序排列
     */
    List<NoteEntity> findByUserIdAndBookIdOrderByCreatedAtDesc(Integer userId, Integer bookId);

    /**
     * 游标分页查询用户笔记（第一页）
     * 按创建时间降序排列，如果创建时间相同则按noteId降序排列
     */
    @Query("SELECT n FROM NoteEntity n WHERE n.userId = :userId " +
           "ORDER BY n.createdAt DESC, n.noteId DESC")
    List<NoteEntity> findUserNotesFirstPage(@Param("userId") Integer userId, PageRequest pageable);

    /**
     * 游标分页查询用户笔记（后续页）
     * cursor 是 noteId，查询创建时间小于等于指定noteId的笔记
     */
    @Query("SELECT n FROM NoteEntity n WHERE n.userId = :userId " +
           "AND (n.createdAt < (SELECT n2.createdAt FROM NoteEntity n2 WHERE n2.noteId = :cursor) " +
           "OR (n.createdAt = (SELECT n2.createdAt FROM NoteEntity n2 WHERE n2.noteId = :cursor) " +
           "AND n.noteId < :cursor)) " +
           "ORDER BY n.createdAt DESC, n.noteId DESC")
    List<NoteEntity> findUserNotesByCursor(@Param("userId") Integer userId, 
                                           @Param("cursor") Integer cursor, 
                                           PageRequest pageable);

    /**
     * 检查指定游标后是否还有更多数据
     */
    @Query("SELECT COUNT(n) > 0 FROM NoteEntity n WHERE n.userId = :userId " +
           "AND (n.createdAt < (SELECT n2.createdAt FROM NoteEntity n2 WHERE n2.noteId = :cursor) " +
           "OR (n.createdAt = (SELECT n2.createdAt FROM NoteEntity n2 WHERE n2.noteId = :cursor) " +
           "AND n.noteId < :cursor))")
    boolean existsMoreAfterCursor(@Param("userId") Integer userId, @Param("cursor") Integer cursor);

    /**
     * 统计用户总笔记数
     */
    Integer countByUserId(Integer userId);

    /**
     * 查询用户最新的N条笔记，按创建时间降序排列
     */
    @Query("SELECT n FROM NoteEntity n WHERE n.userId = :userId " +
           "ORDER BY n.createdAt DESC, n.noteId DESC")
    List<NoteEntity> findTopNByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, PageRequest pageable);

    List<Object[]> findRecentNoteWithBook(Integer userId, PageRequest of);

    Integer countByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime start, LocalDateTime end);
}