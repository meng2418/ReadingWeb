package com.weread.service.external.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.weread.payment.client.WechatPayClient;
import com.weread.service.external.PaymentGatewayService;
import com.weread.vo.asset.PaymentPreOrderVO;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final WechatPayClient wechatPayClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentGatewayServiceImpl(WechatPayClient wechatPayClient) {
        this.wechatPayClient = wechatPayClient;
    }

    @Override
    public PaymentPreOrderVO createWechatOrder(String orderId, Integer amountCents) {
        try {
            String result = wechatPayClient.createOrder(orderId, amountCents);
            JsonNode json = objectMapper.readTree(result);

            String codeUrl = json.has("code_url") ? json.get("code_url").asText() : null;

            if (codeUrl == null) {
                throw new RuntimeException("FakePay 返回缺少二维码");
            }

            return new PaymentPreOrderVO(orderId, codeUrl, "WXPAY_" + orderId);
        } catch (Exception e) {
            throw new RuntimeException("解析 FakePay 失败：" + e.getMessage());
        }
    }

    @Override
    public String queryOrderStatus(String orderId) {
        // 先返回模拟状态
        return "PENDING";
    }
} 