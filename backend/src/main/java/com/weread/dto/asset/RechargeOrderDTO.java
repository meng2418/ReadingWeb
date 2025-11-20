package com.weread.dto.asset;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * RechargeOrderRequestDTO - 创建书币充值订单的请求体
 */
@Data
public class RechargeOrderDTO {
    /** * 充值套餐/金额ID。 
     * 实际项目中，充值通常是选择预设好的套餐（如：30元套餐）。
     */
    @NotNull(message = "充值套餐ID不能为空")
    private Long packageId; 

    /** * 支付方式。 
     * 必须指定使用的支付渠道，如：ALIPAY, WECHATPAY。
     */
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;
    
    /** * 用户IP地址（用于支付风控，Controller层可能自动添加）。
     * (可选) 
     */
    private String clientIp;
    
    // 如果没有套餐ID，而是自定义金额，则需要以下字段：
    // @Min(value = 100, message = "充值金额不能低于1元")
    // private Integer amountCents;
}
