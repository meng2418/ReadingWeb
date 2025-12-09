package com.weread.payment.client;

public interface WechatPayClient {
    String createOrder(String orderId, Integer amountCents);
    String queryOrder(String orderId);
    String refund(String orderId);
}
