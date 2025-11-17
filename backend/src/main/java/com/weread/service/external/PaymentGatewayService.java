package com.weread.service.external;

import com.weread.dto.asset.PaymentPreOrderVO;

/**
 * PaymentGatewayService - 外部支付网关集成服务
 * 封装了与第三方支付平台（如支付宝、微信）的交互逻辑。
 */
public interface PaymentGatewayService {

    /**
     * 创建预支付订单并返回支付所需的URL或App参数。
     * @param orderId 您的系统生成的订单ID
     * @param amountCents 支付金额（分）
     * @param paymentMethod 支付方式 (ALIPAY/WECHATPAY)
     * @param description 订单描述
     * @return 预支付信息
     */
    PaymentPreOrderVO createPreOrder(Long orderId, int amountCents, String paymentMethod, String description);

    /**
     * 查询外部系统中的订单状态。
     * @param outTradeNo 您的系统订单ID (Out Trade No)
     * @param paymentMethod 支付方式 (WECHATPAY/ALIPAY)
     * @param transactionId 支付网关交易号 (可选，用于精确查询)
     * @return 外部系统返回的状态 (例如：PAID, PENDING, FAILED)
     */
    String queryOrderStatus(String outTradeNo, String paymentMethod, String transactionId);
}