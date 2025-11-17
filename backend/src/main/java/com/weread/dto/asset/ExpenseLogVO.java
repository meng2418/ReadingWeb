package com.weread.dto.asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * ExpenseLogVO - 书币消费记录视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseLogVO {
    /** 消费记录ID */
    private Long logId;

    /** 消费描述 (如：购买《三体》) */
    private String description;

    /** 消费的书币数量 */
    private Integer spentCoins;

    /** 消费时间 */
    private LocalDateTime expenseTime;
}
