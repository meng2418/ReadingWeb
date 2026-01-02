package com.weread.entity.asset;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_card")
@Data
public class MemberCardEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer cardId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "card_name", nullable = false, length = 100)
    private String cardName;
    
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
    
    @Column(name = "card_type")
    private String cardType; // 卡类型：experience-体验卡，vip-VIP卡，custom-自定义卡
    
    @Column(name = "is_used")
    private Boolean isUsed = false;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "expire_at")
    private LocalDateTime expireAt;
    
    @Column(name = "source_type", length = 50)
    private String sourceType; // 来源类型：purchase-购买，gift-赠送，reward-奖励，promotion-促销
    
    @Column(name = "source_order_no", length = 64)
    private String sourceOrderNo;
    
    @Column(name = "status")
    private Integer status = 1; // 1-有效，0-已使用，2-已过期，3-已失效
    
    @Column(name = "notes")
    private String notes; // 备注
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // 如果没设置过期时间，默认1年后过期
        if (expireAt == null) {
            expireAt = LocalDateTime.now().plusYears(1);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 状态枚举
    public enum Status {
        VALID(1, "有效"),
        USED(0, "已使用"),
        EXPIRED(2, "已过期"),
        INVALID(3, "已失效");
        
        private final int code;
        private final String description;
        
        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static Status fromCode(int code) {
            for (Status status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return VALID;
        }
    }
    
    // 卡类型枚举
    public enum CardType {
        EXPERIENCE("experience", "体验卡"),
        VIP("vip", "VIP卡"),
        CUSTOM("custom", "自定义卡");
        
        private final String code;
        private final String description;
        
        CardType(String code, String description) {
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
    
    // 来源类型枚举
    public enum SourceType {
        PURCHASE("purchase", "购买"),
        GIFT("gift", "赠送"),
        REWARD("reward", "奖励"),
        PROMOTION("promotion", "促销"),
        SYSTEM("system", "系统赠送");
        
        private final String code;
        private final String description;
        
        SourceType(String code, String description) {
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
    
    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return expireAt != null && expireAt.isBefore(LocalDateTime.now());
    }
    
    /**
     * 检查是否有效
     */
    public boolean isValid() {
        return status == Status.VALID.getCode() && !isExpired() && !isUsed;
    }
}