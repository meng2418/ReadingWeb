package com.weread.repository;

// 补充 JPA 相关注解的导入语句
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.weread.entity.BookshelfBookEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookshelfBookRepository extends JpaRepository<BookshelfBookEntity, Integer> {

    // 根据用户ID查询其书架中的所有书籍关联记录
    List<BookshelfBookEntity> findByUserId(Integer userId);

    // 根据用户ID和书籍ID查询关联记录
    Optional<BookshelfBookEntity> findByUserIdAndBookId(Integer userId, Integer bookId);

    // 根据用户ID和阅读状态查询书籍
    List<BookshelfBookEntity> findByUserIdAndStatus(Integer userId, String status);

    // 从用户书架中删除某本书
    void deleteByUserIdAndBookId(Integer userId, Integer bookId);

    // 更新阅读进度（使用@Query和@Modifying注解）
    @Modifying // 标记为修改操作（非查询）
    @Query("UPDATE BookshelfBookEntity b SET " +
            "b.lastReadTime = :lastReadTime, " +
            "b.chapterIndex = :chapterIndex, " +
            "b.pageNum = :pageNum " +
            "WHERE b.userId = :userId AND b.bookId = :bookId")
    void updateReadingProgress(
            @Param("userId") Integer userId, // 绑定参数名
            @Param("bookId") Integer bookId,
            @Param("chapterIndex") Integer chapterIndex,
            @Param("pageNum") Integer pageNum,
            @Param("lastReadTime") LocalDateTime lastReadTime);
}