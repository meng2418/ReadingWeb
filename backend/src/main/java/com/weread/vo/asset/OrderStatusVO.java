package com.weread.vo.asset;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderStatusVO - 订单状态视图对象
 * 用于查询订单的当前状态和详情。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusVO {
    /** 订单状态 (PENDING, PAID, FAILED, CLOSED) */
    private String status;

    /** 订单金额 (单位：分) */
    private Integer amountCents;

    /** 订单创建时间 */
    private LocalDateTime createdTime;
}
