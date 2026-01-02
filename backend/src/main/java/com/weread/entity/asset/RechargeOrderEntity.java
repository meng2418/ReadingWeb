package com.weread.entity.asset;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_order")
@Data
public class RechargeOrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;  // 订单ID
    
    @Column(name = "order_no", nullable = false, unique = true, length = 32)
    private String orderNo;  // 订单号（业务唯一）
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;  // 用户ID
    
    @Column(name = "package_id", nullable = false)
    private Integer packageId;  // 套餐ID
    
    @Column(name = "coin_amount", nullable = false)
    private Integer coinAmount;  // 充值币数
    
    @Column(name = "bonus_coins", nullable = false)
    private Integer bonusCoins;  // 赠送币数
    
    @Column(name = "pay_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payAmount;  // 支付金额
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;  // 支付方式
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20, nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;  // 订单状态
    
    @Column(name = "transaction_no", length = 64)
    private String transactionNo;  // 第三方支付交易号
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;  // 支付时间
    
    @Column(name = "expire_at")
    private LocalDateTime expireAt;  // 订单过期时间
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联套餐信息（非必须，可以通过packageId查询）
    @Transient
    private RechargePackageEntity packageInfo;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // 生成订单号：时间戳+随机数
        if (orderNo == null) {
            orderNo = generateOrderNo();
        }
        // 设置订单过期时间（30分钟后）
        expireAt = LocalDateTime.now().plusMinutes(30);
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateOrderNo() {
        // 生成订单号：R + 时间戳 + 4位随机数
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 9000) + 1000;
        return "R" + timestamp + random;
    }
    
    // 支付方式枚举
    public enum PaymentMethod {
        WECHAT("wechat", "微信支付"),
        ALIPAY("alipay", "支付宝"),
        UNIONPAY("unionpay", "银联支付");
        
        private final String code;
        private final String description;
        
        PaymentMethod(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static PaymentMethod fromCode(String code) {
            for (PaymentMethod method : values()) {
                if (method.code.equals(code)) {
                    return method;
                }
            }
            throw new IllegalArgumentException("Unknown payment method: " + code);
        }
    }
    
    // 订单状态枚举
    public enum OrderStatus {
        PENDING("pending", "待支付"),
        PAID("paid", "已支付"),
        FAILED("failed", "支付失败"),
        CANCELLED("cancelled", "已取消"),
        EXPIRED("expired", "已过期");
        
        private final String code;
        private final String description;
        
        OrderStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
