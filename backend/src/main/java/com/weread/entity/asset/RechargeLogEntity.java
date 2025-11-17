package com.weread.entity.asset;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class RechargeLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Integer amountCents;
    private Integer acquiredCoins;
    private String status; // SUCCESS, FAILED
    private LocalDateTime rechargeTime;
}
