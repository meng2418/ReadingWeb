package com.weread.service.impl.asset;

import com.weread.entity.asset.TrialBalanceEntity;
import com.weread.repository.asset.TrialBalanceRepository;
import com.weread.service.asset.TrialCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TrialCardServiceImpl implements TrialCardService {

    private final TrialBalanceRepository balanceRepository;
    // private final TrialRewardLogRepository rewardLogRepository; // 假设奖励日志Repository也在这里使用

    @Override
    public TrialBalanceEntity getOrCreateBalance(Long userId) {
        return balanceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    TrialBalanceEntity newBalance = new TrialBalanceEntity();
                    newBalance.setUserId(userId);
                    return balanceRepository.save(newBalance);
                });
    }

    @Override
    public int getTrialDaysBalance(Long userId) {
        return getOrCreateBalance(userId).getDaysBalance();
    }
    
    @Override
    @Transactional
    public TrialBalanceEntity addTrialDays(Long userId, int days, String rewardType) {
        // 【注意】: 完整的逻辑应包含检查 rewardType 和今天的日期，防止重复领取
        
        TrialBalanceEntity balance = getOrCreateBalance(userId);
        balance.setDaysBalance(balance.getDaysBalance() + days);
        // BaseEntity 会自动更新 updatedAt
        
        // 假设这里会记录 TrialRewardLogEntity
        // logRepository.save(...) 

        return balanceRepository.save(balance);
    }
    
    @Override
    @Transactional
    public TrialBalanceEntity clearTrialBalance(Long userId) {
        TrialBalanceEntity balance = getOrCreateBalance(userId);
        if (balance.getDaysBalance() > 0) {
            balance.setDaysBalance(0);
            return balanceRepository.save(balance);
        }
        return balance;
    }
}