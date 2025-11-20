package com.weread.entity.asset;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class ExpenseLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String description;
    private Integer spentCoins; // 消耗书币数量
    private LocalDateTime expenseTime;
}
