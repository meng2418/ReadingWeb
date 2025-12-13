package com.weread.vo.asset;

import com.weread.entity.asset.MemberEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户综合会员状态展示值对象
 * 结合了正式会员和体验卡的全部信息。
 */
@Data
@Builder // 确保使用 builder() 方法
public class MemberStatusVO {

    private Long userId;

    /**
     * 当前是否为有效会员 (包括正式会员或有体验卡余额)
     */
    private Boolean isMember;

    /**
     * 正式会员是否有效 (仅指付费/续费会员)
     */
    private Boolean isFormalMemberActive;

    /**
     * 正式会员过期时间
     */
    private LocalDateTime expireDate;

    /**
     * 剩余体验天数余额
     */
    private Integer trialDaysBalance;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 静态方法：将 Entity 和其他数据组合成 VO
     * @param memberEntity 用户的 MemberEntity (可能为 null/Optional)
     * @param trialDaysBalance 用户的体验卡天数
     * @return MemberStatusVO
     */
    public static MemberStatusVO buildFromEntities(MemberEntity memberEntity, int trialDaysBalance) {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 判断正式会员状态
        boolean isFormalActive = false;
        LocalDateTime expiration = null;
        Integer level = 0;
        
        if (memberEntity != null && memberEntity.getExpireDate() != null) {
            expiration = memberEntity.getExpireDate();
            level = memberEntity.getLevel();
            if (expiration.isAfter(now)) {
                isFormalActive = true;
            }
        }
        
        // 2. 综合判断会员状态
        boolean isMember = isFormalActive || (trialDaysBalance > 0);
        
        return MemberStatusVO.builder()
                .userId(memberEntity != null ? memberEntity.getUserId() : null)
                .isMember(isMember)
                .isFormalMemberActive(isFormalActive)
                .expireDate(expiration)
                .trialDaysBalance(trialDaysBalance)
                .memberLevel(level)
                .build();
    }
}