package com.weread.dto.asset;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembershipPackageDTO {
    
    @NotNull(message = "套餐ID不能为空")
    private Integer packageId;
    
    private String packageName;
    
    private Integer durationDays;
    
    private String originalPrice;
    
    private String discountPrice;
}