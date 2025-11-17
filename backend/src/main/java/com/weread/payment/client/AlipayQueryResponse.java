package com.weread.payment.client;

import lombok.Data;
import lombok.Setter;
/**
 * 支付宝查询接口响应结果
 */
@Data
public class AlipayQueryResponse {
    private String tradeStatus; // 交易状态 (TRADE_SUCCESS, WAIT_BUYER_PAY, etc.)
    private String outTradeNo;
    private String tradeNo; // 支付宝交易号

    @Setter
    private boolean success; // 请求是否成功
}
