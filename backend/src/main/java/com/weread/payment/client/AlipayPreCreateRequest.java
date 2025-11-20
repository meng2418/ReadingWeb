package com.weread.payment.client;

import lombok.Data;

/**
 * 对应支付宝 SDK 的 trade.precreate 请求对象
 */
@Data
public class AlipayPreCreateRequest {
    private String outTradeNo;
    private String totalAmount; // 字符串，单位：元
    private String subject;
    private String productCode;
    private String notifyUrl;
    // ... 其他必要的参数
}

