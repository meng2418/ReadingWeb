package com.weread.service.impl.user;

import com.weread.entity.user.AchievementLogEntity;
import com.weread.entity.user.AchievementType;
import com.weread.repository.user.AchievementLogRepository;
import com.weread.service.user.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementLogRepository achievementLogRepository;

    // --- 成就步进配置 ---
    // Key: 成就类型, Value: 步进值
    private static final Map<AchievementType, Integer> STEP_VALUES = Map.of(
        AchievementType.BOOK_COUNT, 20,         // 每 20 本书
        AchievementType.NOTE_COUNT, 50,         // 每 50 条笔记
        AchievementType.DURATION,   6000        // 每 100 小时 (100 * 60 分钟)
    );
    
    // 最大等级限制，防止无限生成日志
    private static final int MAX_LEVEL = 100; 
    

    @Override
    @Transactional
    public void checkAndLogAchievement(Long userId, AchievementType type, int currentValue) {
        
        Integer step = STEP_VALUES.get(type);
        if (step == null || step <= 0) {
            return; // 无效或未配置
        }
        
        // 1. 计算用户当前应该达到的最高级别 (N * step <= currentValue)
        // 例如：CurrentValue=45，Step=20，则 TargetLevel = 45 / 20 = 2
        int targetLevel = currentValue / step;
        
        if (targetLevel == 0) {
            return; // 未达到第一个成就
        }
        
        // 2. 查找用户该项成就的当前最高已记录级别
        Optional<AchievementLogEntity> latestLogOpt = achievementLogRepository.findTopByUserIdAndTypeOrderByLevelDesc(userId, type);
        int currentLevel = latestLogOpt.map(AchievementLogEntity::getLevel).orElse(0);

        // 3. 检查是否有新成就达成
        if (targetLevel > currentLevel) {
            
            // 记录所有新达成的级别，从 currentLevel + 1 循环到 targetLevel
            for (int newLevel = currentLevel + 1; newLevel <= targetLevel && newLevel <= MAX_LEVEL; newLevel++) {
                
                int thresholdValue = newLevel * step;
                
                AchievementLogEntity newLog = new AchievementLogEntity();
                newLog.setUserId(userId);
                newLog.setType(type);
                newLog.setLevel(newLevel);
                newLog.setValueSnapshot(currentValue); // 记录当前值
                newLog.setDescription(buildDescription(type, thresholdValue));
                
                achievementLogRepository.save(newLog);
            }
        }
    }

    private String buildDescription(AchievementType type, int thresholdValue) {
        // 根据类型格式化显示单位
        return switch (type) {
            case BOOK_COUNT -> String.format("累计阅读 %d 本书", thresholdValue);
            case NOTE_COUNT -> String.format("累计 %d 条笔记", thresholdValue);
            case DURATION -> String.format("累计阅读时长 %d 小时", thresholdValue / 60); // 转换为小时显示
        };
    }
    
    /**
     * 【核心查询逻辑】
     * 获取用户阅历版块的显示记录：最新达成且级别最高的事件
     */
    @Override
    public List<AchievementLogEntity> getAchievementsByUserId(Long userId) {
        // 1. 获取用户所有已达成的成就记录
        List<AchievementLogEntity> allLogs = achievementLogRepository.findAllByUserIdOrderByAchievedAtDesc(userId);

        if (allLogs.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 按成就类型分组，找出每个类型的最高级别
        Map<AchievementType, AchievementLogEntity> highestLevelLogs = allLogs.stream()
            .collect(Collectors.toMap(
                AchievementLogEntity::getType,
                log -> log,
                (existing, replacement) -> {
                    // 合并函数：保留级别更高的那个（如果级别相同，保留时间最新的，虽然 OrderByAchievedAtDesc 已经做了初步排序）
                    return (replacement.getLevel() > existing.getLevel()) ? replacement : existing;
                }
            ));

        // 3. 将最高级别的记录集合转换为 List
        List<AchievementLogEntity> highestLogs = highestLevelLogs.values().stream().toList();

        // 4. 从这三个最高级别记录中，找到达成时间（AchievedAt）最新的那个
        Optional<AchievementLogEntity> latestHighestLog = highestLogs.stream()
            .max(Comparator.comparing(AchievementLogEntity::getAchievedAt));

        // 5. 返回包含最新最高成就的列表
        return latestHighestLog.map(List::of).orElse(Collections.emptyList());
    }
}