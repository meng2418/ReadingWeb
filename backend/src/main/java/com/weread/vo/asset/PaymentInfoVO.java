package com.weread.vo.asset;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentInfoVO {
    private Integer packageId;
    private Integer coinAmount;
    private BigDecimal cnyAmount;  // 人民币金额
    private Integer bonusCoins;
    private BigDecimal payAmount;   // 实际支付金额
    private Integer totalCoins;     // 总获得币数 = coinAmount + bonusCoins
}
