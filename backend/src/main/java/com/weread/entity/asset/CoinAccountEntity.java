package com.weread.entity.asset;

import com.weread.entity.base.BaseEntity;
import com.weread.entity.user.UserEntity;
import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "coin_account_info")
public class CoinAccountEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    
    @Column(unique = true, nullable = false)
    private Long userId;
    
    private Integer balance = 0; // 充值币余额

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "coinAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoinTransactionEntity> transactions;
}