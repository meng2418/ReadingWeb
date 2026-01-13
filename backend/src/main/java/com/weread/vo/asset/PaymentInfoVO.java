package com.weread.vo.asset;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentInfoVO {
    private Integer packageId;
    private Integer coinAmount;
    private BigDecimal cnyAmount;  // 人民币金额
    private Integer bonusCoins;
    private BigDecimal payAmount;   // 实际支付金额
    private Integer totalCoins;     // 总获得币数 = coinAmount + bonusCoins
    private List<String> paymentMethods;  // 支持的支付方式：wechat, alipay, unionpay
}
