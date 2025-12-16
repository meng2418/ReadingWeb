package com.weread.service.impl.user;

import com.weread.entity.user.AchievementType;
import com.weread.entity.user.UserStatEntity;
import com.weread.repository.user.UserStatRepository;
import com.weread.service.user.AchievementService; // 依赖注入的成就服务
import com.weread.service.user.UserStatService;
import com.weread.vo.user.UserStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStatServiceImpl implements UserStatService {

    private final UserStatRepository userStatRepository;
    private final AchievementService achievementService; // 注入成就服务

    /**
     * 获取或创建用户统计实体
     */
    private UserStatEntity getOrCreateUserStat(Long userId) {
        Optional<UserStatEntity> statOpt = userStatRepository.findByUserId(userId);
        if (statOpt.isPresent()) {
            return statOpt.get();
        }
        // 创建新的统计记录
        UserStatEntity newStat = new UserStatEntity();
        newStat.setUserId(userId);
        return userStatRepository.save(newStat);
    }
    
    @Override
    public UserStatVO getUserStats(Long userId) {
        UserStatEntity entity = getOrCreateUserStat(userId);
        return UserStatVO.fromEntity(entity);
    }

    @Override
    @Transactional
    public void incrementBookCount(Long userId, int count) {
        UserStatEntity stat = getOrCreateUserStat(userId);
        stat.setTotalBooksCompleted(stat.getTotalBooksCompleted() + count);
        userStatRepository.save(stat);

        // 触发书籍成就检查
        achievementService.checkAndLogAchievement(
            userId, 
            AchievementType.BOOK_COUNT, 
            stat.getTotalBooksCompleted()
        );
    }

    @Override
    @Transactional
    public void incrementNoteCount(Long userId, int count) {
        UserStatEntity stat = getOrCreateUserStat(userId);
        stat.setTotalNotesCount(stat.getTotalNotesCount() + count);
        userStatRepository.save(stat);

        // 触发笔记成就检查
        achievementService.checkAndLogAchievement(
            userId, 
            AchievementType.NOTE_COUNT, 
            stat.getTotalNotesCount()
        );
    }

    @Override
    @Transactional
    public void addReadingDuration(Long userId, int minutes) {
        UserStatEntity stat = getOrCreateUserStat(userId);
        stat.setTotalReadingDurationMinutes(stat.getTotalReadingDurationMinutes() + minutes);
        userStatRepository.save(stat);

        // 触发时长成就检查
        achievementService.checkAndLogAchievement(
            userId, 
            AchievementType.DURATION, 
            stat.getTotalReadingDurationMinutes()
        );
    }
}