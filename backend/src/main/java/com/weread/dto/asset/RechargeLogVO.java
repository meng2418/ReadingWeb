package com.weread.dto.asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RechargeLogVO - 书币充值记录视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeLogVO {
    /** 充值记录ID */
    private Long logId;

    /** 充值金额 (单位：分) */
    private Integer amountCents;

    /** 获得的最终书币数 */
    private Integer acquiredCoins;

    /** 充值时间 */
    private LocalDateTime rechargeTime;

    /** 状态 (成功/失败) */
    private String status;
}
