package com.weread.repository.community;

import com.weread.entity.community.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    /**
     * 查找特定的点赞记录 (通用)
     */
    Optional<LikeEntity> findByTargetTypeAndTargetIdAndUserId(String targetType, Long targetId, Long userId);

    /**
     * 删除特定的点赞记录 (通用)
     */
    void deleteByTargetTypeAndTargetIdAndUserId(String targetType, Long targetId, Long userId);

    /**
     * 【✅ 新增方法】根据目标类型 (POST/COMMENT) 和目标ID统计总点赞数。
     * Spring Data JPA 会自动实现 SELECT COUNT(*) WHERE targetType = ? AND targetId = ?
     */
    long countByTargetTypeAndTargetId(String targetType, Long targetId);

    /**
     * 【✅ 修正后的方法】查询最新点赞的 N 个用户的 ID 列表。
     * 使用 @Query 明确返回 userId，并使用原生 SQL 的 LIMIT 语法（在 JPQL 中，
     * 我们通常结合 Pageable/原生查询，这里为了简洁使用 JPQL 配合 LIMIT 逻辑）
     * * 注意：在 H2/PostgreSQL/MySQL 等数据库中，原生 SQL LIMIT 常用。
     * 在标准 JPA/Spring Data 中，更推荐使用 Pageable 对象来限制数量。
     */
    @Query(value = "SELECT l.userId FROM LikeEntity l " +
                   "WHERE l.targetType = :type AND l.targetId = :id " +
                   "ORDER BY l.createdAt DESC")
    List<Long> findTopNUserIdsByTarget(@Param("type") String targetType, 
                                       @Param("id") Long targetId,
                                       Pageable pageable); // 使用 Pageable 来限制数量
}