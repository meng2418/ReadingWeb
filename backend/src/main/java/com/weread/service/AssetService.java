package com.weread.service;

import com.weread.dto.asset.CreateOrderDTO;
import com.weread.dto.asset.RechargeOrderDTO;
import com.weread.vo.asset.CoinBalanceVO;
import com.weread.vo.asset.ExpenseLogVO;
import com.weread.vo.asset.MemberPackageVO;
import com.weread.vo.asset.MemberStatusVO;
import com.weread.vo.asset.OrderStatusVO;
import com.weread.vo.asset.PaymentPreOrderVO;
import com.weread.vo.asset.RechargeLogVO;

import java.util.List;

/**
 * AssetService - 资产和会员业务服务
 * 负责会员权益、订单、充值、消费等财务相关逻辑。
 */
public interface AssetService {
    
    // ===========================================
    // 1. 会员体系 (Membership System)
    // ===========================================
    
    /**
     * 查询当前用户会员状态和权益截止日期
     */
    
    MemberStatusVO getMemberStatus(Long userId);

    /**
     * 获取所有可用的会员套餐列表
     */
    List<MemberPackageVO> getMemberPackages();
    
    /**
     * 创建会员开通/续费订单
     * 返回预支付信息 (如支付URL或订单ID)
     */
    PaymentPreOrderVO createMemberOrder(Long userId, CreateOrderDTO request);

    /**
     * 查询指定订单的支付状态 (会员订单或充值订单通用)
     */
    OrderStatusVO getOrderStatus(Long userId, Long orderId);


    // ===========================================
    // 2. 我的资产 (User Assets)
    // ===========================================

    /**
     * 查询用户的当前书币余额
     */
    CoinBalanceVO getCoinBalance(Long userId);

    /**
     * 创建书币充值订单
     */
    PaymentPreOrderVO createRechargeOrder(Long userId, RechargeOrderDTO request);

    /**
     * 分页查询书币充值记录
     */
    List<RechargeLogVO> getRechargeLogs(Long userId, int page, int size);

    /**
     * 分页查询书币消费记录
     */
    List<ExpenseLogVO> getExpenseLogs(Long userId, int page, int size);
}