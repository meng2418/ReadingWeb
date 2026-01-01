package com.weread.vo.asset;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MembershipPackageVO {
    
    private Integer packageId;
    
    private String packageName;
    
    private Integer durationDays;
    
    private BigDecimal originalPrice;
    
    private BigDecimal discountPrice;
}