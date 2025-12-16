package com.weread.service.asset;

import com.weread.entity.asset.TrialBalanceEntity;

public interface TrialCardService {
    TrialBalanceEntity getOrCreateBalance(Long userId);
    TrialBalanceEntity addTrialDays(Long userId, int days, String rewardType);
    int getTrialDaysBalance(Long userId);
    TrialBalanceEntity clearTrialBalance(Long userId);
}