package com.weread.service.impl.reading;

import com.weread.dto.reading.ReadingRecordDTO;
import com.weread.entity.ReadingProgressEntity;
import com.weread.entity.user.UserStatEntity;
import com.weread.repository.ReadingProgressRepository;
import com.weread.repository.user.UserStatRepository;
import com.weread.service.reading.ReadingService;
import com.weread.vo.reading.ReadingStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 阅读服务实现类
 */
@Service
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingProgressRepository progressRepository;
    private final UserStatRepository userStatRepository;

    @Override
    @Transactional
    public void recordReadingProgress(Long userId, ReadingRecordDTO dto) {
        // 查找或创建阅读进度记录
        Optional<ReadingProgressEntity> progressOpt = 
                progressRepository.findByUserIdAndBookId(userId, dto.getBookId());

        ReadingProgressEntity progress;
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
        } else {
            progress = new ReadingProgressEntity();
            progress.setUserId(userId);
            progress.setBookId(dto.getBookId());
        }

        // 更新进度信息
        progress.setChapterId(dto.getChapterId());
        progress.setCurrentPage(dto.getCurrentPage() != null ? dto.getCurrentPage() : 1);
        progress.setProgress(dto.getProgress() != null ? dto.getProgress() : 0f);
        progress.setLastReadAt(LocalDateTime.now());

        progressRepository.save(progress);
    }

    @Override
    public ReadingStatVO getReadingStatistics(Long userId) {
        UserStatEntity stat = userStatRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserStatEntity newStat = new UserStatEntity();
                    newStat.setUserId(userId);
                    return userStatRepository.save(newStat);
                });

        ReadingStatVO vo = new ReadingStatVO();
        vo.setUserId(userId);
        vo.setTotalReadingMinutes(stat.getTotalReadingDurationMinutes());
        
        // 计算今日阅读时长（这里简化处理，实际应该从阅读记录中统计）
        vo.setTodayReadingMinutes(0);
        
        // 计算连续阅读天数（简化处理）
        vo.setConsecutiveDays(0);
        
        // 总阅读书籍数（可以从书架或阅读进度中统计）
        vo.setTotalBooksRead(0);

        return vo;
    }

    @Override
    @Transactional
    public void recordReadingDuration(Long userId, Integer minutes) {
        UserStatEntity stat = userStatRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserStatEntity newStat = new UserStatEntity();
                    newStat.setUserId(userId);
                    return userStatRepository.save(newStat);
                });

        stat.setTotalReadingDurationMinutes(
                stat.getTotalReadingDurationMinutes() + minutes
        );

        userStatRepository.save(stat);
    }
}

