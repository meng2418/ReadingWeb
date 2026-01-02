package com.weread.service.asset;

import com.weread.dto.asset.RechargeRequestDTO;
import com.weread.vo.asset.PaymentInfoVO;
import com.weread.vo.asset.RechargeResponseVO;
import com.weread.entity.asset.RechargeOrderEntity;

public interface RechargeService {
    
    /**
     * 获取支付页信息
     */
    PaymentInfoVO getPaymentPageInfo(Integer packageId, Integer userId);
    
    /**
     * 创建充值订单
     */
    RechargeResponseVO createRechargeOrder(RechargeRequestDTO request, Integer userId);
    
    /**
     * 支付成功回调
     */
    boolean handlePaymentCallback(String orderNo, String transactionNo);
    
    /**
     * 检查订单状态
     */
    RechargeOrderEntity.OrderStatus checkOrderStatus(String orderNo, Integer userId);
}