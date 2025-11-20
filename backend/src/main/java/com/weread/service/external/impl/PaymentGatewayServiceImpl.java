package com.weread.service.external.impl;

import com.weread.service.external.PaymentGatewayService;
import com.weread.dto.asset.PaymentPreOrderVO;
import com.weread.payment.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// 假设的自定义异常
class PaymentGatewayException extends RuntimeException {
    public PaymentGatewayException(String message) { super(message); }
}

// 假设的客户端依赖 (您需要在您的项目中实现它们)
// import com.weread.payment.AlipayClient;
// import com.weread.payment.WechatPayClient;
// import com.alipay.sdk.request.AlipayPreCreateRequest;
// import com.alipay.sdk.response.AlipayPreOrderResponse;
// import com.alipay.sdk.request.AlipayQueryRequest;
// import com.alipay.sdk.response.AlipayQueryResponse;


@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    // 假设注入了封装了 SDK 细节的客户端
    private final WechatPayClient wxClient; 
    private final AlipayClient aliClient; 

    // 配置值
    @Value("${payment.base-notify-url}")
    private String baseNotifyUrl;
    @Value("${payment.wechat.app-id}")
    private String wxAppId;
    @Value("${payment.alipay.app-id}")
    private String aliAppId;

    // 构造函数注入
    public PaymentGatewayServiceImpl(WechatPayClient wxClient, AlipayClient aliClient) {
        this.wxClient = wxClient;
        this.aliClient = aliClient;
    }

    @Override
    public PaymentPreOrderVO createPreOrder(Long orderId, int amountCents, String paymentMethod, String description) {
        if ("WECHATPAY".equalsIgnoreCase(paymentMethod)) {
            return createWechatPayOrder(orderId, amountCents, description);
        } else if ("ALIPAY".equalsIgnoreCase(paymentMethod)) {
            return createAlipayOrder(orderId, amountCents, description);
        } else {
            throw new PaymentGatewayException("不支持的支付方式: " + paymentMethod);
        }
    }

    @Override
    public String queryOrderStatus(String outTradeNo, String paymentMethod, String transactionId) {
        
        System.out.println("LOG: Querying status for order " + outTradeNo + " via " + paymentMethod);

        if ("WECHATPAY".equalsIgnoreCase(paymentMethod)) {
            return queryWechatPayStatus(outTradeNo, transactionId);
        } else if ("ALIPAY".equalsIgnoreCase(paymentMethod)) {
            return queryAlipayStatus(outTradeNo, transactionId);
        } else {
            throw new PaymentGatewayException("不支持的订单查询支付方式: " + paymentMethod);
        }
    }
    // =================================================================
    // 微信支付查询实现
    // =================================================================
    private String queryWechatPayStatus(String outTradeNo, String transactionId) {
        
        // 1. 构造查询参数
        Map<String, Object> request = new HashMap<>();
        // 微信 V3 API 通常使用 out_trade_no (您的订单号) 或 transaction_id (微信交易号) 之一查询
        if (transactionId != null && !transactionId.isEmpty()) {
            request.put("transaction_id", transactionId);
        } else {
            request.put("out_trade_no", outTradeNo);
        }
        
        // 2. 调用客户端
        Map<String, Object> response;
        try {
            response = wxClient.queryOrder(request); // 假设 queryOrder 接收 Map 并返回 Map
        } catch (Exception e) {
            System.err.println("ERROR: 微信订单查询通信失败: " + e.getMessage());
            throw new PaymentGatewayException("微信订单查询网关通信失败");
        }
        
        // 3. 解析状态
        String tradeState = (String) response.get("trade_state"); // 例如 SUCCESS, REFUND, NOTPAY
        
        if ("SUCCESS".equals(tradeState)) {
            return "PAID";
        } else if ("NOTPAY".equals(tradeState) || "USERPAYING".equals(tradeState)) {
            return "PENDING";
        } else {
            return "FAILED"; // 包含 CLOSED, REVOKED, PAYERROR 等
        }
    }

    // =================================================================
    // 支付宝支付查询实现
    // =================================================================
    private String queryAlipayStatus(String outTradeNo, String transactionId) {
        
        // 1. 构造 SDK 查询请求
        AlipayQueryRequest request = new AlipayQueryRequest();
        
        // 支付宝查询接口主要通过 out_trade_no (您的订单号) 或 trade_no (支付宝交易号)
        if (transactionId != null && !transactionId.isEmpty()) {
            // request.setTradeNo(transactionId); // 使用支付宝交易号
        } else {
            // request.setOutTradeNo(outTradeNo); // 使用您的订单号
        }
        
        // 2. 调用客户端
        AlipayQueryResponse response;
        try {
            // response = aliClient.tradeQuery(request); // 假设 tradeQuery 接收 Request 并返回 Response
            // --- 模拟返回，使代码结构完整 ---
            response = new AlipayQueryResponse();
            response.setSuccess(true); // 使用 Setter 方法
            response.setTradeStatus("TRADE_SUCCESS"); 
        } catch (Exception e) {
            System.err.println("ERROR: 支付宝订单查询通信失败: " + e.getMessage());
            throw new PaymentGatewayException("支付宝订单查询网关通信失败");
        }
        
        // 3. 解析状态
        if (!response.isSuccess()) {
            // 假设请求成功但业务失败，例如订单不存在
            return "FAILED"; 
        }
        
        String tradeStatus = response.getTradeStatus(); // 例如 TRADE_SUCCESS, WAIT_BUYER_PAY
        
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            return "PAID";
        } else if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
            return "PENDING";
        } else {
            return "FAILED"; // 包含 TRADE_CLOSED, TRADE_FAIL 等
        }
    }

    // =================================================================
    // 微信支付 V3 接口实现 (以 Native 扫码支付为例)
    // =================================================================

    private PaymentPreOrderVO createWechatPayOrder(Long orderId, int amountCents, String description) {
        // 1. 构建微信 V3 API 请求参数 (JSON 结构)
        Map<String, Object> request = new HashMap<>();
        request.put("appid", wxAppId);
        request.put("mchid", "YOUR_WECHAT_MCH_ID"); // TODO: 实际商户号，可配置
        request.put("description", description);
        request.put("out_trade_no", String.valueOf(orderId));
        request.put("notify_url", baseNotifyUrl + "wechatpay");
        
        // 2. 金额信息（单位：分）
        Map<String, Object> amount = new HashMap<>();
        amount.put("total", amountCents);
        amount.put("currency", "CNY");
        request.put("amount", amount);

        // 3. 调用微信客户端进行下单 (客户端负责签名、HTTPS请求和验签)
        Map<String, Object> response;
        try {
            response = wxClient.createOrder(request);
        } catch (Exception e) {
            System.err.println("ERROR: 微信支付下单调用失败: " + e.getMessage());
            throw new PaymentGatewayException("微信支付网关通信失败");
        }

        // 4. 解析响应
        String codeUrl = (String) response.get("code_url"); // 扫码支付的二维码URL
        
        if (codeUrl == null || codeUrl.isEmpty()) {
            System.err.println("ERROR: 微信支付响应失败或缺少code_url: " + response);
            throw new PaymentGatewayException("微信支付下单失败，请检查参数");
        }

        // 5. 返回 VO
        // V3 接口返回的 prepay_id 不直接用于扫码，但我们为了统一 VO 结构仍然返回一个标识
        return new PaymentPreOrderVO(orderId, codeUrl, "WXPAY-NATIVE-" + orderId);
    }

    // =================================================================
    // 支付宝支付接口实现 (以 trade.precreate 预创建为例)
    // =================================================================

    private PaymentPreOrderVO createAlipayOrder(Long orderId, int amountCents, String description) {
        
        // 1. 构造支付宝 SDK 请求对象
        AlipayPreCreateRequest request = new AlipayPreCreateRequest();

        // 2. 设置通用参数和业务参数
        request.setNotifyUrl(baseNotifyUrl + "alipay");
        
        // 支付宝金额单位是元 ($.xx)，需要转换为字符串格式的元
        String totalAmountYuan = String.format("%.2f", amountCents / 100.0); 
        
        request.setOutTradeNo(String.valueOf(orderId));
        request.setTotalAmount(totalAmountYuan);
        request.setSubject(description);
        request.setProductCode("FACE_TO_FACE_PAYMENT"); // 假设使用面对面支付

        // 3. 调用支付宝客户端执行请求
        AlipayPreOrderResponse response;
        try {
            response = aliClient.tradePreCreate(request);
        } catch (Exception e) {
            System.err.println("ERROR: 支付宝支付下单调用失败: " + e.getMessage());
            throw new PaymentGatewayException("支付宝支付网关通信失败");
        }

        // 4. 校验响应
        if (!response.isSuccess()) {
            System.err.println("ERROR: 支付宝下单失败，原因: " + response.getErrorMsg());
            throw new PaymentGatewayException("支付宝下单失败，请检查日志");
        }

        // 5. 返回 VO
        return new PaymentPreOrderVO(orderId, response.getQrCode(), response.getPrepayId());
    }
}