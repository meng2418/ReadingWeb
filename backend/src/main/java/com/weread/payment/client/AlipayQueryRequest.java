package com.weread.payment.client;

import lombok.Data;
/**
 * 对应支付宝 SDK 的 trade.query 请求对象
 */
@Data
public class AlipayQueryRequest {
    private String outTradeNo; // 您的订单号
    private String tradeNo; // 支付宝交易号
}
