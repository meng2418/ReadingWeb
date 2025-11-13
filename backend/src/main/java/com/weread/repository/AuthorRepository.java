package com.weread.repository;

import com.weread.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 作者数据访问接口（操作 author_info 表）
 */
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {

    /**
     * 根据作者ID查询作者信息
     * （继承自 JpaRepository 的 findById() 方法已实现此功能，此处可省略，仅作说明）
     * 
     * @param authorId 作者ID
     * @return 作者实体（Optional 包装，避免空指针）
     */
    @Override
    Optional<AuthorEntity> findById(Integer authorId);

    /**
     * 根据作者姓名查询作者信息（支持唯一校验）
     * 
     * @param name 作者姓名（数据库中唯一）
     * @return 作者实体
     */
    Optional<AuthorEntity> findByName(String name);

    /**
     * 检查作者姓名是否已存在（用于新增/修改时的唯一校验）
     * 
     * @param name 作者姓名
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByName(String name);
}