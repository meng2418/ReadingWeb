package com.weread.entity.asset;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class OrderEntity {
    @Id
    private Long orderId; // 订单ID
    private Long userId;
    private String description; // 订单描述
    private String type; // 订单类型 (MEMBER, RECHARGE)
    private String status; // 订单状态 (PENDING, PAID, FAILED)
    private Integer amountCents; // 订单金额
    private String paymentMethod;
    private String prepayId; // 支付网关预支付ID
    private String transactionId; // 支付网关交易号
    private Long packageId;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
