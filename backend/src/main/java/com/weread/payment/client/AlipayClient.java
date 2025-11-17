package com.weread.payment.client;

/**
 * AlipayClient - 支付宝开放平台 API 抽象客户端
 * 封装了 RSA 签名、JSON 序列化和通信细节。
 */
public interface AlipayClient {
    
    // 预创建接口（对应 trade.precreate）
    AlipayPreOrderResponse tradePreCreate(AlipayPreCreateRequest request);
    
    // 订单查询接口（对应 trade.query）
    AlipayQueryResponse tradeQuery(AlipayQueryRequest request);
}