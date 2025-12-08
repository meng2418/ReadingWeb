package com.weread.dto.asset;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 充值请求数据传输对象
 */
@Data
public class CoinDepositDTO {

    /**
     * 充值金额（RMB，单位：元）
     * 假设前端传递的是以“元”为单位的金额，例如：10, 50, 100
     */
    @NotNull(message = "充值金额不能为空")
    @Min(value = 1, message = "充值金额不能少于 1 元")
    private Integer moneyAmount;
    
    // 在实际的生产环境中，这里还会包含：
    // private String paymentMethod; // 支付方式 (WeChat, Alipay)
    // private String transactionType; // 交易类型 (模拟充值/真实充值)
}