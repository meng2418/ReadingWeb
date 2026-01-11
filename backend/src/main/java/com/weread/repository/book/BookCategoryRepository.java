package com.weread.repository.book;

import com.weread.entity.book.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    /**
     * 根据父分类ID查询子分类
     */
    List<CategoryEntity> findByParentIdOrderBySortOrderDesc(Integer parentId);

    /**
     * 查询顶级分类（parentId为0或null）
     */
    List<CategoryEntity> findByParentIdIsNullOrParentIdOrderBySortOrderDesc(Integer parentId);

    /**
     * 根据分类名称查询
     */
    Optional<CategoryEntity> findByName(String name);

    /**
     * 查询所有启用的分类
     */
    List<CategoryEntity> findByIsEnabledTrueOrderBySortOrderDesc();
}
