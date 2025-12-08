package com.weread.vo.asset;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 交易收据或结果值对象 (Value Object)
 * 用于用户充值（Deposit）操作完成后，返回给前端的结果凭证。
 */
@Data
public class TransactionReceiptVO {

    /**
     * 内部或第三方支付网关的交易ID，用于追踪
     */
    private String transactionId; 
    
    /**
     * 充值金额（原始人民币金额，单位：元或分，这里假设是元，与服务层模拟对应）
     */
    private Integer depositedAmount; 
    
    /**
     * 实际获得的充值币数量
     */
    private Integer coinsGranted;    
    
    /**
     * 充值操作完成后的用户最新充值币余额
     */
    private Integer newBalance;      
    
    /**
     * 交易完成的时间戳
     */
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * 交易状态：SUCCESS, FAILED, PROCESSING
     */
    private String status = "SUCCESS";
    
    /**
     * 交易结果的描述信息
     */
    private String message = "充值成功";
}