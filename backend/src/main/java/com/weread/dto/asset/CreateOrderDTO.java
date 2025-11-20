package com.weread.dto.asset;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * OrderRequestDTO - 创建会员/充值订单的通用请求体
 */
@Data
public class CreateOrderDTO {
    /** 购买的套餐ID */
    @NotNull(message = "套餐ID不能为空")
    private Long packageId;

    /** 支付方式 (如：ALIPAY, WECHATPAY) */
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;
}
