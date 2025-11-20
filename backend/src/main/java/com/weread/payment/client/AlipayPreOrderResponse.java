package com.weread.payment.client;

import lombok.Data;

/**
 * 支付宝预创建接口响应结果
 */
@Data
public class AlipayPreOrderResponse {
    private String qrCode; // 预创建成功后的二维码链接
    private String tradeStatus; // 交易状态（虽然预创建通常是成功或失败）
    private boolean isSuccess; // 请求是否成功 (SDK 验签后判断)
    private String errorMsg; // 错误信息
    
    // 并非支付宝的标准字段，但用于简化流程
    private String prepayId; 
}

