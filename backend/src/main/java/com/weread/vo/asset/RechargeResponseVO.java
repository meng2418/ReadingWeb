package com.weread.vo.asset;

import lombok.Data;

@Data
public class RechargeResponseVO {
    private String orderId;        // 订单ID（字符串）
    private Integer coinAmount;    // 充值币数
    private Integer bonusCoins;    // 赠送币数
}
