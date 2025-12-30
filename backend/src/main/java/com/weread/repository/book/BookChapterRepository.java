package com.weread.repository.book;

import com.weread.entity.book.BookChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookChapterRepository extends JpaRepository<BookChapterEntity, Integer> {

    /**
     * 根据书籍ID查询所有章节，按章节序号排序
     */
    List<BookChapterEntity> findByBookIdOrderByChapterNumberAsc(Integer bookId);

    /**
     * 根据书籍ID和章节序号查询章节
     */
    Optional<BookChapterEntity> findByBookIdAndChapterNumber(Integer bookId, Integer chapterNumber);

    /**
     * 根据书籍ID查询已发布的章节数量
     */
    @Query("SELECT COUNT(c) FROM BookChapterEntity c WHERE c.bookId = :bookId AND c.isPublished = true")
    Long countPublishedChaptersByBookId(@Param("bookId") Integer bookId);

    /**
     * 根据书籍ID查询总字数
     */
    @Query("SELECT COALESCE(SUM(c.wordCount), 0) FROM BookChapterEntity c WHERE c.bookId = :bookId")
    Integer sumWordCountByBookId(@Param("bookId") Integer bookId);

    /**
     * 查询上一章
     */
    Optional<BookChapterEntity> findByBookIdAndChapterNumberLessThanOrderByChapterNumberDesc(
            Integer bookId, Integer chapterNumber);

    /**
     * 查询下一章
     */
    Optional<BookChapterEntity> findByBookIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(
            Integer bookId, Integer chapterNumber);
}

