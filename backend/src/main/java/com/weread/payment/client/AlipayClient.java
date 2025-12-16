package com.weread.payment.client;

import java.util.Map;

public interface AlipayClient {

    Map<String, Object> createOrder(Map<String, Object> request);

    Map<String, Object> queryOrder(Map<String, Object> request);
}
