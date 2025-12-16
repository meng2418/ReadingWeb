package com.weread.entity.asset;

import com.weread.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "coin_transaction_info")
public class CoinTransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    /** 映射数据库字段 account_id（只读字段） */
    @Column(name = "account_id", nullable = false)
    private Integer accountId; 

    private Integer amount; // 交易数量 (充值: 正, 消费: 负)

    private String type; // PURCHASE, DEPOSIT, REWARD

    private String description;

    /** 正确关联 CoinAccountEntity */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private CoinAccountEntity coinAccount;
}
