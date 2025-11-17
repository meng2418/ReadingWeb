package com.weread.dto.asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CoinBalanceVO - 书币余额视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinBalanceVO {
    /** 当前书币余额 */
    private Integer coins;
}
