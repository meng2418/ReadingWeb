package com.weread.repository;

import com.weread.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Book Data Access Interface.
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    /**
     * Finds a book by its Book ID.
     * This method can be used to validate if a book exists when adding/querying a book.
     */
    Optional<BookEntity> findByBookId(Integer bookId);

    /**
     * Finds a book by its ISBN (used for unique validation).
     */
    Optional<BookEntity> findByIsbn(String isbn);

    /**
     * 根据分类ID查询书籍
     */
    Page<BookEntity> findByCategoryIdAndIsPublishedTrue(Integer categoryId, Pageable pageable);

    /**
     * 查询热门书籍（按阅读量排序）
     */
    @Query("SELECT b FROM BookEntity b WHERE b.isPublished = true ORDER BY b.readCount DESC")
    Page<BookEntity> findPopularBooks(Pageable pageable);

    /**
     * 查询免费书籍
     */
    Page<BookEntity> findByIsFreeTrueAndIsPublishedTrue(Pageable pageable);

    /**
     * 查询付费书籍
     */
    Page<BookEntity> findByIsFreeFalseAndIsPublishedTrue(Pageable pageable);

    /**
     * 查询会员专属书籍
     */
    Page<BookEntity> findByIsMemberOnlyTrueAndIsPublishedTrue(Pageable pageable);

    /**
     * 根据多个条件筛选书籍
     */
    @Query("SELECT b FROM BookEntity b WHERE " +
           "b.isPublished = true AND " +
           "(:categoryId IS NULL OR b.categoryId = :categoryId) AND " +
           "(:isFree IS NULL OR b.isFree = :isFree) AND " +
           "(:isMemberOnly IS NULL OR b.isMemberOnly = :isMemberOnly)")
    Page<BookEntity> findBooksWithFilters(
            @Param("categoryId") Integer categoryId,
            @Param("isFree") Boolean isFree,
            @Param("isMemberOnly") Boolean isMemberOnly,
            Pageable pageable);


    /**
     * 搜索书籍（按标题或作者名）
     */
    @Query("SELECT b FROM BookEntity b " +
           "LEFT JOIN b.author a " +
           "WHERE (b.title LIKE %:keyword% OR " +
           "       (a IS NOT NULL AND a.name LIKE %:keyword%)) " +
           "AND b.isPublished = true " +
           "ORDER BY b.readCount DESC, b.createdAt DESC")
    Page<BookEntity> searchBooks(@Param("keyword") String keyword, Pageable pageable);
}