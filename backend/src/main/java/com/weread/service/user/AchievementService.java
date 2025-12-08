package com.weread.service.user;

import com.weread.entity.user.AchievementLogEntity;
import com.weread.entity.user.AchievementType;

import java.util.List;

public interface AchievementService {

    void checkAndLogAchievement(Long userId, AchievementType type, int currentValue);
    
    List<AchievementLogEntity> getAchievementsByUserId(Long userId);
    
}