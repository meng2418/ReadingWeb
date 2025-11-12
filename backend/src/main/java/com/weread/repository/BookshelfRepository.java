package com.weread.repository;

import com.weread.entity.BookshelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 书架主表数据访问接口
 * （若书架设计为“用户-分类”结构，用于管理用户的多个书架分类）
 */
@Repository
public interface BookshelfRepository extends JpaRepository<BookshelfEntity, Integer> {

    /**
     * 根据用户ID查询其所有书架分类
     * （例如：查询用户的“正在阅读”“已购”等书架）
     */
    List<BookshelfEntity> findByUserId(Integer userId);

    /**
     * 检查用户是否已存在同名书架（避免重复创建）
     */
    boolean existsByUserIdAndShelfName(Integer userId, String shelfName);

    /**
     * 根据用户ID和书架名称查询具体书架
     */
    Optional<BookshelfEntity> findByUserIdAndShelfName(Integer userId, String shelfName);
}