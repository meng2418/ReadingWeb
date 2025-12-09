package com.weread.service.external;

import com.weread.vo.asset.PaymentPreOrderVO;

public interface PaymentGatewayService {

    /**
     * 创建预订单，返回二维码链接/支付参数
     */
    PaymentPreOrderVO createWechatOrder(String orderId, Integer amountCents);

    /**
     * 查询支付状态（SUCCESS/PENDING/FAILED）
     */
    String queryOrderStatus(String orderId);
}
