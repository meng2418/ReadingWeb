package com.weread.repository;

import com.weread.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 书籍数据访问接口
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    /**
     * 根据书籍ID查询书籍
     * （书架模块核心方法：添加/查询书籍时需验证书籍是否存在）
     */
    Optional<BookEntity> findByBookId(Integer bookId);

    /**
     * 根据ISBN查询书籍（用于校验书籍唯一性）
     */
    Optional<BookEntity> findByIsbn(String isbn);
}