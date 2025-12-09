package com.weread.payment.client.impl;

import com.weread.payment.client.WechatPayClient;
import org.springframework.stereotype.Component;

@Component
public class FakeWechatPayClient implements WechatPayClient {

    @Override
    public String createOrder(String orderId, Integer amountCents) {
        return "{ \"code_url\": \"https://fakepay.test/qrcode/" + orderId + "\" }";
    }

    @Override
    public String queryOrder(String orderId) {
        return "{ \"status\": \"SUCCESS\" }";
    }

    @Override
    public String refund(String orderId) {
        return "{ \"result\": \"REFUND_SUCCESS\" }";
    }
}
