package com.weread.repository.book;

import com.weread.entity.book.BookCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategoryEntity, Integer> {

    /**
     * 根据父分类ID查询子分类
     */
    List<BookCategoryEntity> findByParentIdOrderBySortOrderDesc(Integer parentId);

    /**
     * 查询顶级分类（parentId为0或null）
     */
    List<BookCategoryEntity> findByParentIdIsNullOrParentIdOrderBySortOrderDesc(Integer parentId);

    /**
     * 根据分类名称查询
     */
    Optional<BookCategoryEntity> findByName(String name);

    /**
     * 查询所有启用的分类
     */
    List<BookCategoryEntity> findByIsEnabledTrueOrderBySortOrderDesc();
}

