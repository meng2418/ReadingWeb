package com.weread.vo.asset;

import com.weread.entity.asset.TrialBalanceEntity;
import lombok.Builder;
import lombok.Data;

/**
 * 用户体验卡余额展示值对象 (Value Object)
 * 用于向前端展示用户剩余的免费体验天数。
 */
@Data
@Builder
public class TrialBalanceVO {

    private Long userId;

    /**
     * 剩余体验天数余额
     */
    private Integer daysBalance; 

    /**
     * 静态方法：将 Entity 转换为 VO
     * @param entity 数据库中的 TrialBalanceEntity
     * @return TrialBalanceVO
     */
    public static TrialBalanceVO fromEntity(TrialBalanceEntity entity) {
        return TrialBalanceVO.builder()
                .userId(entity.getUserId())
                .daysBalance(entity.getDaysBalance())
                .build();
    }
}