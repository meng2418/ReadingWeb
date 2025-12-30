package com.weread.repository.user;

import com.weread.entity.user.FollowEntity;
import com.weread.entity.user.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Integer> {

    // 查询某用户关注的用户列表（分页/游标方式）
    @Query("SELECT u FROM UserEntity u JOIN FollowEntity f ON u.userId = f.followingId "
            + "WHERE f.followerId = :userId "
            + "AND (:cursor IS NULL OR u.userId > :cursor) "
            + "ORDER BY u.userId ASC")
    List<UserEntity> findFollowing(
            @Param("userId") Integer userId,
            @Param("cursor") String cursor,
            @Param("limit") int limit);

    /**
     * 查找特定的关注记录，用于判断是否已关注
     */
    Optional<FollowEntity> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    /**
     * 删除关注关系（取消关注）
     */
    void deleteByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    // --- 计数和列表查询 ---

    /**
     * 统计某个用户（followingId）的粉丝数
     */
    int countByFollowingId(Integer followingId);

    /**
     * 统计某个用户（followerId）的关注数
     */
    int countByFollowerId(Integer followerId);

    /**
     * 获取某个用户（userId）的关注者列表（粉丝列表）
     */
    Page<FollowEntity> findByFollowingId(Integer followingId, Pageable pageable);

    /**
     * 获取某个用户（userId）的关注列表（ta 关注了谁）
     */
    Page<FollowEntity> findByFollowerId(Integer followerId, Pageable pageable);
}