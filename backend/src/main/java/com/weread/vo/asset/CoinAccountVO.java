package com.weread.vo.asset;

import com.weread.entity.asset.CoinAccountEntity;
import lombok.Builder;
import lombok.Data;

/**
 * 充值币账户展示值对象 (Value Object)
 * 用于向前端展示用户的充值币余额。
 */
@Data
@Builder
public class CoinAccountVO {

    private Long userId;

    /**
     * 当前充值币余额
     */
    private Integer balance;

    /**
     * 静态方法：将 Entity 转换为 VO
     * @param entity 数据库中的 CoinAccountEntity
     * @return CoinAccountVO
     */
    public static CoinAccountVO fromEntity(CoinAccountEntity entity) {
        return CoinAccountVO.builder()
                .userId(entity.getUserId())
                .balance(entity.getBalance())
                .build();
    }
}