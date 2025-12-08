package com.weread.service.asset;

import com.weread.entity.asset.CoinAccountEntity;
import com.weread.vo.asset.TransactionReceiptVO;

public interface CoinService {
    CoinAccountEntity getOrCreateAccount(Long userId);
    void purchaseBook(Long userId, Long bookId, int bookPrice);
    TransactionReceiptVO processSimulatedDeposit(Long userId, int moneyAmount); // 模拟充值
}