package com.weread.repository.user;

import com.weread.entity.user.FollowEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    /**
     * 查找特定的关注记录，用于判断是否已关注
     */
    Optional<FollowEntity> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 删除关注关系（取消关注）
     */
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // --- 计数和列表查询 ---

    /**
     * 统计某个用户（followingId）的粉丝数
     */
    long countByFollowingId(Long followingId);

    /**
     * 统计某个用户（followerId）的关注数
     */
    long countByFollowerId(Long followerId);

    /**
     * 获取某个用户（userId）的关注者列表（粉丝列表）
     */
    Page<FollowEntity> findByFollowingId(Long followingId, Pageable pageable);

    /**
     * 获取某个用户（userId）的关注列表（ta 关注了谁）
     */
    Page<FollowEntity> findByFollowerId(Long followerId, Pageable pageable);
}