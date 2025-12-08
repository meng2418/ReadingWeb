package com.weread.service.impl.asset;

import com.weread.entity.book.UserBookAccessEntity;
import com.weread.entity.asset.CoinAccountEntity;
import com.weread.entity.asset.CoinTransactionEntity;
import com.weread.repository.book.UserBookAccessRepository;
import com.weread.repository.asset.CoinAccountRepository;
import com.weread.repository.asset.CoinTransactionRepository;
import com.weread.service.asset.CoinService;
import com.weread.vo.asset.TransactionReceiptVO; // 假设此VO存在
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {
    
    private final CoinAccountRepository accountRepository;
    private final CoinTransactionRepository transactionRepository;
    private final UserBookAccessRepository userBookAccessRepository;

    @Override
    public CoinAccountEntity getOrCreateAccount(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CoinAccountEntity newAccount = new CoinAccountEntity();
                    newAccount.setUserId(userId);
                    return accountRepository.save(newAccount);
                });
    }

    // 内部方法：核心入账逻辑
    private CoinAccountEntity depositCoinInternal(Long userId, int coinAmount, String description, String type) {
        CoinAccountEntity account = getOrCreateAccount(userId);
        account.setBalance(account.getBalance() + coinAmount);
        accountRepository.save(account);

        CoinTransactionEntity transaction = new CoinTransactionEntity();
        transaction.setAccountId(account.getAccountId());
        transaction.setAmount(coinAmount);
        transaction.setType(type);
        transaction.setDescription(description);
        transactionRepository.save(transaction);
        
        return account;
    }

    @Override
    @Transactional
    public TransactionReceiptVO processSimulatedDeposit(Long userId, int moneyAmount) {
        // 假设 1元 = 1充值币 (1:100 分)
        int coinsGranted = moneyAmount; 
        
        CoinAccountEntity account = depositCoinInternal(
            userId, 
            coinsGranted, 
            String.format("用户充值 %d RMB (模拟)", moneyAmount),
            "DEPOSIT"
        );
        
        // 构造并返回收据
        TransactionReceiptVO receipt = new TransactionReceiptVO();
        receipt.setDepositedAmount(moneyAmount);
        receipt.setCoinsGranted(coinsGranted);
        receipt.setNewBalance(account.getBalance());
        receipt.setTimestamp(LocalDateTime.now());
        receipt.setStatus("SUCCESS");
        
        return receipt;
    }

    @Override
    @Transactional
    public void purchaseBook(Long userId, Long bookId, int bookPrice) {
        CoinAccountEntity account = getOrCreateAccount(userId);

        if (userBookAccessRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new IllegalArgumentException("您已购买此书籍。");
        }
        if (account.getBalance() < bookPrice) {
            throw new RuntimeException("充值币余额不足，需要 " + bookPrice);
        }
        
        // 1. 扣除充值币
        int amount = -bookPrice;
        account.setBalance(account.getBalance() + amount); 
        accountRepository.save(account);

        // 2. 授予永久阅读权限
        UserBookAccessEntity access = new UserBookAccessEntity();
        access.setUserId(userId);
        access.setBookId(bookId);
        userBookAccessRepository.save(access);

        // 3. 记录消费交易
        CoinTransactionEntity transaction = new CoinTransactionEntity();
        transaction.setAccountId(account.getAccountId());
        transaction.setAmount(amount); 
        transaction.setType("PURCHASE");
        transaction.setDescription("购买书籍ID: " + bookId);
        transactionRepository.save(transaction);
    }
}