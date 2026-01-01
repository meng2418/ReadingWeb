package com.weread.vo.asset;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MembershipPaymentInfoVO {
    
    private Integer packageId;
    
    private String packageName;
    
    private Integer durationDays;
    
    private BigDecimal originalAmount;
    
    private BigDecimal discountAmount;
    
    private List<String> paymentMethods;  // wechat, alipay, balance, card
    
}