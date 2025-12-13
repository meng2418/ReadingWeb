package com.weread.repository.user;

import com.weread.entity.user.AchievementLogEntity;
import com.weread.entity.user.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementLogRepository extends JpaRepository<AchievementLogEntity, Long> {

    /**
     * 查找某个用户某项成就的最高级别记录
     */
    Optional<AchievementLogEntity> findTopByUserIdAndTypeOrderByLevelDesc(Long userId, AchievementType type);
    
    /**
     * 获取用户所有已达成的成就记录
     */
    List<AchievementLogEntity> findAllByUserIdOrderByAchievedAtDesc(Long userId);
}