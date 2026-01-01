package com.weread.dto.asset;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class PurchaseMembershipDTO {
    
    @NotNull(message = "套餐ID不能为空")
    private Integer packageId;
    
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;  // wechat, alipay, balance, card
    
}