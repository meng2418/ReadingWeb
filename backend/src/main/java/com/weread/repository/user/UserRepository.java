package com.weread.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weread.entity.user.UserEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByPhone(String phone);
    boolean existsByPhone(String phone);
    Optional<UserEntity> findByUserId(Integer userId);
    Optional<UserEntity> findByUsername(String username);
    
    /**
     * 【新增】更新用户的 isMember 状态
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.isMember = :isMemberStatus WHERE u.userId = :userId")
    int updateIsMemberStatus(@Param("userId") Integer userId, @Param("isMemberStatus") Boolean isMemberStatus);

    /**
     * 关注者（follower）的关注数 (followingCount) +1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followingCount = u.followingCount + 1 WHERE u.userId = :userId")
    void incrementFollowingCount(@Param("userId") Integer userId);

    /**
     * 被关注者（following）的粉丝数 (followerCount) +1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followerCount = u.followerCount + 1 WHERE u.userId = :userId")
    void incrementFollowerCount(@Param("userId") Integer userId);

    /**
     * 关注者（follower）的关注数 (followingCount) -1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followingCount = u.followingCount - 1 WHERE u.userId = :userId AND u.followingCount > 0")
    void decrementFollowingCount(@Param("userId") Integer userId);

    /**
     * 被关注者（following）的粉丝数 (followerCount) -1
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.followerCount = u.followerCount - 1 WHERE u.userId = :userId AND u.followerCount > 0")
    void decrementFollowerCount(@Param("userId") Integer userId);

    // 查询用户币数
    @Query("SELECT u.coins FROM UserEntity u WHERE u.userId = :userId")
    Optional<Integer> findCoinsByUserId(@Param("userId") Integer userId);
    
    // 增加用户币数
    @Modifying
    @Query("UPDATE UserEntity u SET u.coins = u.coins + :amount, u.updatedAt = CURRENT_TIMESTAMP WHERE u.userId = :userId")
    int addCoins(@Param("userId") Integer userId, @Param("amount") Integer amount);
    
    // 扣减用户币数（需要确保余额充足）
    @Modifying
    @Query("UPDATE UserEntity u SET u.coins = u.coins - :amount, u.updatedAt = CURRENT_TIMESTAMP WHERE u.userOd = :userId AND u.coins >= :amount")
    int deductCoins(@Param("userId") Integer userId, @Param("amount") Integer amount);
    boolean existsByUsername(String username);
    void updateLastLoginTime(Integer userId, LocalDateTime now);    
}