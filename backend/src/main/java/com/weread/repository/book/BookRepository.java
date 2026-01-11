package com.weread.repository.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weread.entity.book.BookEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Book Data Access Interface.
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

       /**
        * Finds a book by its Book ID.
        * This method can be used to validate if a book exists when adding/querying a
        * book.
        */
       Optional<BookEntity> findByBookId(Integer bookId);

       @Query("SELECT b FROM BookEntity b WHERE b.bookId IN :bookIds")
       List<BookEntity> findByBookIds(@Param("bookIds") List<Integer> bookIds);

       /**
        * Finds a book by its ISBN (used for unique validation).
        */
       Optional<BookEntity> findByIsbn(String isbn);

       /**
        * 根据书籍ID查询封面
        */
       @Query("SELECT b.cover FROM BookEntity b WHERE b.bookId = :bookId")
       String findCoverUrlByBookId(@Param("bookId") Integer bookId);

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
       List<BookEntity> findByAuthorIdAndIsPublishedTrue(Long authorId);

       /**
        * 统计作者的作品数量
        */
       int countByAuthorIdAndIsPublishedTrue(Long authorId);

       /**
        * 根据作者ID查询该作者的代表作（排除指定书籍，按阅读量排序，最多返回指定数量）
        * 
        * @param authorId      作者ID
        * @param excludeBookId 排除的书籍ID
        * @param pageable      分页参数（用于限制返回数量）
        * @return 作者代表作列表
        */
       @Query("SELECT b FROM BookEntity b WHERE b.authorId = :authorId " +
                     "AND b.isPublished = true " +
                     "AND b.bookId <> :excludeBookId " +
                     "ORDER BY b.readCount DESC, b.rating DESC")
       List<BookEntity> findAuthorRepresentativeWorks(
                     @Param("authorId") Long authorId,
                     @Param("excludeBookId") Integer excludeBookId,
                     Pageable pageable);

       /**
        * 根据分类ID查询相关推荐书籍（排除指定书籍，按阅读量和评分排序，最多返回指定数量）
        * 
        * @param categoryId    分类ID
        * @param excludeBookId 排除的书籍ID
        * @param pageable      分页参数（用于限制返回数量）
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

       /**
        * 随机获取6本书籍 (MySQL)
        */
       @Query(value = "SELECT * FROM book_info ORDER BY RAND() LIMIT 6", nativeQuery = true)
       List<BookEntity> findRandomBooks();

       /**
        * 获取书籍总数
        */
       @Query("SELECT COUNT(b) FROM BookEntity b")
       long countAllBooks();

       /**
        * 获取所有书籍ID（用于内存随机算法）
        */
       @Query("SELECT b.id FROM BookEntity b")
       List<Integer> findAllBookIds();

       // 榜单
       // 1. 获取所有已发布的书籍
       @Query("SELECT b FROM BookEntity b WHERE b.isPublished = true")
       List<BookEntity> findAllPublishedBooks();

       // 2. 新书榜：最近3个月创建的书籍（created_at=datetime → 保留LocalDateTime）
       @Query("SELECT b FROM BookEntity b " +
                     "WHERE b.isPublished = true " +
                     "AND b.createdAt >= :startDate " +
                     "ORDER BY COALESCE(b.rating, 0) DESC, b.readCount DESC")
       List<BookEntity> findNewBooksByDate(
                     @Param("startDate") LocalDateTime startDate, // 保持不变：匹配created_at=datetime
                     Pageable pageable);

       // 3. 按评分排序
       @Query("SELECT b FROM BookEntity b " +
                     "WHERE b.isPublished = true " +
                     "AND b.rating IS NOT NULL " +
                     "ORDER BY b.rating DESC, b.readCount DESC")
       List<BookEntity> findBooksByRating(Pageable pageable);

       // 4. 按阅读人数排序
       @Query("SELECT b FROM BookEntity b " +
                     "WHERE b.isPublished = true " +
                     "ORDER BY b.readCount DESC, COALESCE(b.rating, 0) DESC")
       List<BookEntity> findBooksByReadCount(Pageable pageable);

       // 5. 获取书籍的阅读记录（用于计算近期热度）
       // 核心修改：参数改为LocalDate（匹配read_date=date），JPQL中ur.readDate会自动适配
       @Query("SELECT ur.bookId, COUNT(ur.id) as recentReadCount, " +
                     "MAX(ur.readDate) as latestReadTime " +
                     "FROM UserReadingRecordEntity ur " +
                     "WHERE ur.readDate >= :startDate " +
                     "GROUP BY ur.bookId")
       List<Object[]> findRecentReadStats(@Param("startDate") LocalDate startDate); // 修改：LocalDate替代LocalDateTime

       // 6. 综合排序：评分 + 阅读人数 + 创建时间
       @Query("SELECT b FROM BookEntity b " +
                     "WHERE b.isPublished = true " +
                     "ORDER BY " +
                     "  (COALESCE(b.rating, 0) * 0.7 + " +
                     "   COALESCE(b.readCount, 0) * 0.3) DESC")
       List<BookEntity> findBooksByCompositeScore(Pageable pageable);

       // 搜索相关代码

       // 根据标题模糊搜索（忽略大小写）
       // 新的方法：使用 JOIN FETCH 立即加载 author
       @Query("SELECT DISTINCT b FROM BookEntity b " +
                     "LEFT JOIN FETCH b.author " +
                     "WHERE UPPER(b.title) LIKE UPPER(CONCAT('%', :title, '%'))")
       List<BookEntity> findByTitleContainingIgnoreCaseWithAuthor(@Param("title") String title);

       // 如果需要搜索作者名
       @Query("SELECT b FROM BookEntity b WHERE b.author.authorName LIKE %:keyword%")
       List<BookEntity> findByAuthorNameContaining(@Param("keyword") String keyword);

       // 获取作者的代表作
       List<BookEntity> findTop3ByAuthorIdOrderByRatingDesc(Long authorId);
}