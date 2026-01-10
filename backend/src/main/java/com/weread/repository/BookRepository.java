package com.weread.repository;

import com.weread.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
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
           "       (a IS NOT NULL AND a.authorName LIKE %:keyword%)) " +
           "AND b.isPublished = true " +
           "ORDER BY b.readCount DESC, b.createdAt DESC")
    Page<BookEntity> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据作者ID查询该作者的所有作品
     */
    List<BookEntity> findByAuthorIdAndIsPublishedTrue(Integer authorId);

    /**
     * 统计作者的作品数量
     */
    int countByAuthorIdAndIsPublishedTrue(Integer authorId);

    /**
     * 根据作者ID查询该作者的代表作（排除指定书籍，按阅读量排序，最多返回指定数量）
     * @param authorId 作者ID
     * @param excludeBookId 排除的书籍ID
     * @param pageable 分页参数（用于限制返回数量）
     * @return 作者代表作列表
     */
    @Query("SELECT b FROM BookEntity b WHERE b.authorId = :authorId " +
           "AND b.isPublished = true " +
           "AND b.bookId <> :excludeBookId " +
           "ORDER BY b.readCount DESC, b.rating DESC")
    List<BookEntity> findAuthorRepresentativeWorks(
            @Param("authorId") Integer authorId,
            @Param("excludeBookId") Integer excludeBookId,
            Pageable pageable);

    /**
     * 根据分类ID查询相关推荐书籍（排除指定书籍，按阅读量和评分排序，最多返回指定数量）
     * @param categoryId 分类ID
     * @param excludeBookId 排除的书籍ID
     * @param pageable 分页参数（用于限制返回数量）
     * @return 相关推荐书籍列表
     */
    @Query("SELECT b FROM BookEntity b WHERE b.categoryId = :categoryId " +
           "AND b.isPublished = true " +
           "AND b.bookId <> :excludeBookId " +
           "ORDER BY b.readCount DESC, b.rating DESC")
    List<BookEntity> findRelatedBooksByCategory(
            @Param("categoryId") Integer categoryId,
            @Param("excludeBookId") Integer excludeBookId,
            Pageable pageable);
}