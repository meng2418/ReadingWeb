package com.weread.vo.asset;

import lombok.Data;

@Data
public class RechargePackageVO {
    private Integer coinAmount;   // 充值币数
    private Double cnyAmount;     // 人民币数
    private Integer bonusCoins;   // 赠送充值币数
}
