package com.weread.entity.asset;

import com.weread.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "coin_transaction_info")
public class CoinTransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false)
    private Long accountId; // 关联到 CoinAccount

    private Integer amount; // 交易数量 (充值: 正, 消费: 负)
    
    private String type; // PURCHASE, DEPOSIT, REWARD
    
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId", insertable = false, updatable = false)
    private CoinAccountEntity coinAccount;
}