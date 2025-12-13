package com.weread.vo.asset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端扫码支付界面需要的字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPreOrderVO {

    /**
     * 用户订单号
     */
    private String orderId;

    /**
     * 二维码 URL（用于前端生成二维码）
     */
    private String qrCodeUrl;

    /**
     * 第三方支付平台的预支付 ID（可为空，但为了统一结构预留）
     */
    private String prePayId;
}
