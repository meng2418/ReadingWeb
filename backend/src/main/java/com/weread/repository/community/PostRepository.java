package com.weread.repository.community;

import com.weread.entity.community.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// 继承 JpaSpecificationExecutor 用于支持动态查询 (筛选 all, mine, following)
public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {
    
    // 自定义方法：查找作者 ID 在列表中的帖子（用于 following 筛选）
    List<PostEntity> findByAuthorIdIn(List<Long> authorIds);
}