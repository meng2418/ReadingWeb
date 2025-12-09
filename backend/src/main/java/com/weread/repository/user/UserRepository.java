package com.weread.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weread.entity.user.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhone(String phone);
    boolean existsByPhone(String phone);
    Optional<UserEntity> findByUserId(Long userId);
    
    /**
     * 【新增】更新用户的 isMember 状态
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.isMember = :isMemberStatus WHERE u.userId = :userId")
    int updateIsMemberStatus(@Param("userId") Long userId, @Param("isMemberStatus") Boolean isMemberStatus);

    /**
     * 关注者（follower）的关注数 (followingCount) +1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followingCount = u.followingCount + 1 WHERE u.userId = :userId")
    void incrementFollowingCount(@Param("userId") Long userId);

    /**
     * 被关注者（following）的粉丝数 (followerCount) +1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followerCount = u.followerCount + 1 WHERE u.userId = :userId")
    void incrementFollowerCount(@Param("userId") Long userId);

    /**
     * 关注者（follower）的关注数 (followingCount) -1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followingCount = u.followingCount - 1 WHERE u.userId = :userId AND u.followingCount > 0")
    void decrementFollowingCount(@Param("userId") Long userId);

    /**
     * 被关注者（following）的粉丝数 (followerCount) -1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followerCount = u.followerCount - 1 WHERE u.userId = :userId AND u.followerCount > 0")
    void decrementFollowerCount(@Param("userId") Long userId);
    
}