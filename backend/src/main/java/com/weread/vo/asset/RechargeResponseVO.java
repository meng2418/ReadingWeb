package com.weread.vo.asset;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RechargeResponseVO {
    private String orderId;        // 订单ID（字符串）
    private BigDecimal payAmount;  // 支付金额
    private Integer coinAmount;    // 充值币数
    private Integer bonusCoins;    // 赠送币数
    private Integer currentCoins;  // 当前总币数
    private String paymentMethod;  // 支付方式
    private String payUrl;         // 支付跳转URL（可选）
    private String qrCode;         // 支付二维码（可选）
}
