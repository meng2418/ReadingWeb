package com.weread.payment.client;

import java.util.Map;

/**
 * WechatPayClient - 微信支付 V3 API 抽象客户端
 * 封装了签名、证书、HTTP通信等底层细节。
 */
public interface WechatPayClient {
    
    // 统一下单接口，接收和返回标准的Map/JSON结构
    Map<String, Object> createOrder(Map<String, Object> requestParams);
    
    // 订单查询接口，接收Map或outTradeNo，返回包含状态的Map
    Map<String, Object> queryOrder(Map<String, Object> requestParams); 
}