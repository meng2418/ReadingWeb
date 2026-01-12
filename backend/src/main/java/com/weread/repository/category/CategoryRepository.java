package com.weread.repository.category;

import com.weread.entity.book.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    /**
     * 根据分类名称查询
     */
    CategoryEntity findByName(String name);
}
