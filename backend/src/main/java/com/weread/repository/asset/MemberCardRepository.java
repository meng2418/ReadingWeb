package com.weread.repository.asset;

import com.weread.entity.asset.MemberCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberCardRepository extends JpaRepository<MemberCardEntity, Integer> {
    
    /**
     * 统计用户有效会员卡数量
     */
    @Query("SELECT COUNT(c) FROM MemberCardEntity c WHERE c.userId = :userId AND c.status = 1 AND c.expireAt > :now")
    Integer countValidCardsByUserId(@Param("userId") Integer userId, @Param("now") LocalDateTime now);
    
    /**
     * 查询用户所有会员卡
     */
    List<MemberCardEntity> findByUserId(Integer userId);
    
    /**
     * 查询用户会员卡（分页）
     */
    List<MemberCardEntity> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * 查询用户有效会员卡
     */
    @Query("SELECT c FROM MemberCardEntity c WHERE c.userId = :userId AND c.status = 1 AND c.expireAt > :now ORDER BY c.expireAt ASC")
    List<MemberCardEntity> findValidCardsByUserId(@Param("userId") Integer userId, @Param("now") LocalDateTime now);
    
    /**
     * 查询用户未使用的有效会员卡
     */
    @Query("SELECT c FROM MemberCardEntity c WHERE c.userId = :userId AND c.isUsed = false AND c.status = 1 AND c.expireAt > :now ORDER BY c.createdAt ASC")
    List<MemberCardEntity> findUnusedValidCardsByUserId(@Param("userId") Integer userId, @Param("now") LocalDateTime now);
    
    /**
     * 查询用户已使用的会员卡
     */
    List<MemberCardEntity> findByUserIdAndIsUsedTrue(Integer userId);
    
    /**
     * 根据订单号查询会员卡
     */
    Optional<MemberCardEntity> findBySourceOrderNo(String sourceOrderNo);
    
    /**
     * 根据来源类型查询会员卡
     */
    List<MemberCardEntity> findByUserIdAndSourceType(Integer userId, String sourceType);
    
    /**
     * 根据卡类型查询会员卡
     */
    List<MemberCardEntity> findByUserIdAndCardType(Integer userId, String cardType);
    
    /**
     * 查询即将过期的会员卡（指定天数内到期）
     */
    @Query("SELECT c FROM MemberCardEntity c WHERE c.userId = :userId AND c.status = 1 AND c.expireAt BETWEEN :startDate AND :endDate")
    List<MemberCardEntity> findExpiringCards(@Param("userId") Integer userId, 
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查询7天内过期的会员卡
     */
    @Query("SELECT c FROM MemberCardEntity c WHERE c.userId = :userId AND c.status = 1 AND c.expireAt BETWEEN :now AND :sevenDaysLater")
    List<MemberCardEntity> findCardsExpiringIn7Days(@Param("userId") Integer userId,
                                                  @Param("now") LocalDateTime now,
                                                  @Param("sevenDaysLater") LocalDateTime sevenDaysLater);
    
    /**
     * 标记会员卡为已使用
     */
    @Modifying
    @Query("UPDATE MemberCardEntity c SET c.isUsed = true, c.usedAt = :usedAt, c.status = 0, c.updatedAt = CURRENT_TIMESTAMP WHERE c.cardId = :cardId AND c.status = 1")
    int markAsUsed(@Param("cardId") Integer cardId, @Param("usedAt") LocalDateTime usedAt);
    
    /**
     * 批量标记会员卡为已使用
     */
    @Modifying
    @Query("UPDATE MemberCardEntity c SET c.isUsed = true, c.usedAt = :usedAt, c.status = 0, c.updatedAt = CURRENT_TIMESTAMP WHERE c.cardId IN :cardIds AND c.status = 1")
    int batchMarkAsUsed(@Param("cardIds") List<Integer> cardIds, @Param("usedAt") LocalDateTime usedAt);
    
    /**
     * 更新过期状态（将过期的卡标记为已过期）
     */
    @Modifying
    @Query("UPDATE MemberCardEntity c SET c.status = 2, c.updatedAt = CURRENT_TIMESTAMP WHERE c.expireAt < :now AND c.status = 1")
    int updateExpiredStatus(@Param("now") LocalDateTime now);
    
    /**
     * 检查用户是否有可用的体验卡
     */
    @Query("SELECT COUNT(c) > 0 FROM MemberCardEntity c WHERE c.userId = :userId AND c.cardType = 'experience' AND c.isUsed = false AND c.status = 1 AND c.expireAt > :now")
    boolean hasAvailableExperienceCard(@Param("userId") Integer userId, @Param("now") LocalDateTime now);
    
    /**
     * 获取用户最早的有效体验卡
     */
    @Query("SELECT c FROM MemberCardEntity c WHERE c.userId = :userId AND c.cardType = 'experience' AND c.isUsed = false AND c.status = 1 AND c.expireAt > :now ORDER BY c.expireAt ASC LIMIT 1")
    Optional<MemberCardEntity> findEarliestExperienceCard(@Param("userId") Integer userId, @Param("now") LocalDateTime now);
    
    /**
     * 统计用户会员卡总数
     */
    @Query("SELECT COUNT(c) FROM MemberCardEntity c WHERE c.userId = :userId")
    Integer countByUserId(@Param("userId") Integer userId);
    
    /**
     * 统计用户已使用的会员卡数量
     */
    @Query("SELECT COUNT(c) FROM MemberCardEntity c WHERE c.userId = :userId AND c.isUsed = true")
    Integer countUsedCardsByUserId(@Param("userId") Integer userId);
    
    /**
     * 统计用户即将过期的会员卡数量
     */
    @Query("SELECT COUNT(c) FROM MemberCardEntity c WHERE c.userId = :userId AND c.status = 1 AND c.expireAt BETWEEN :now AND :futureDate")
    Integer countExpiringCards(@Param("userId") Integer userId, 
                             @Param("now") LocalDateTime now, 
                             @Param("futureDate") LocalDateTime futureDate);
    
    /**
     * 检查是否有相同来源的会员卡
     */
    boolean existsByUserIdAndSourceTypeAndSourceOrderNo(Integer userId, String sourceType, String sourceOrderNo);
}