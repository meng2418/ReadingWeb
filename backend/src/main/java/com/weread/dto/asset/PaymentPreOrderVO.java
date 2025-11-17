package com.weread.dto.asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentPreOrderVO - 支付预订单信息视图对象
 * 返回给前端用于唤起支付或展示二维码。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPreOrderVO {
    /** 后端生成的订单ID */
    private Long orderId;

    /** 用于跳转或展示二维码的支付URL */
    private String paymentUrl;

    /** 预支付ID，可能用于App端调起支付 */
    private String prepayId;
}
