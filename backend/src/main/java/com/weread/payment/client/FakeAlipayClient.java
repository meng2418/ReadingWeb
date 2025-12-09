package com.weread.payment.client;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component  // ⭐注册为 Spring Bean
public class FakeAlipayClient implements AlipayClient {

    private final Map<String, String> orderStatus = new HashMap<>();

    @Override
    public Map<String, Object> createOrder(Map<String, Object> request) {

        String orderId = (String) request.get("out_trade_no");
        orderStatus.put(orderId, "WAIT_BUYER_PAY");

        Map<String, Object> response = new HashMap<>();
        response.put("qr_code", "https://sandbox.alipay.fake/pay/" + orderId);
        response.put("status", "SUCCESS");

        System.out.println("[FAKE ALIPAY] create order: " + orderId);
        return response;
    }

    @Override
    public Map<String, Object> queryOrder(Map<String, Object> request) {

        String orderId = (String) request.get("out_trade_no");

        Map<String, Object> response = new HashMap<>();
        response.put("trade_status", orderStatus.getOrDefault(orderId, "NOT_FOUND"));

        System.out.println("[FAKE ALIPAY] query order: " + orderId);

        return response;
    }
}
