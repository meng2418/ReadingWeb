package com.weread.service.impl.user;

import com.weread.entity.user.UserReadingRecordEntity;
import com.weread.entity.user.ReadingRewardEntity;
import com.weread.entity.asset.MemberCardEntity;
import com.weread.repository.user.UserReadingRecordRepository;
import com.weread.repository.user.ReadingRewardRepository;
import com.weread.repository.asset.MemberCardRepository;
import com.weread.service.user.ReadingService;
import com.weread.vo.user.ReadingTimeStatItemVO;
import com.weread.vo.user.TodayReadingTimeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingServiceImpl implements ReadingService {
    
    private final UserReadingRecordRepository readingRecordRepository;
    private final ReadingRewardRepository readingRewardRepository;
    private final MemberCardRepository memberCardRepository;
    
    @Override
    @Transactional(readOnly = true)
    public TodayReadingTimeVO getTodayReadingTime(Integer userId) {
        LocalDate today = LocalDate.now();
        
        // 查询今日阅读时长
        Integer todayReadingTime = readingRecordRepository.sumReadingTimeByUserIdAndDate(userId, today);
        if (todayReadingTime == null) {
            todayReadingTime = 0;
        }
        
        // 构建返回对象
        TodayReadingTimeVO vo = new TodayReadingTimeVO();
        vo.setReadingTime(todayReadingTime);
        vo.setDailyGoal(30); // 默认每日目标30分钟
        
        // 计算完成率
        if (vo.getDailyGoal() > 0) {
            int rate = (int) ((todayReadingTime * 100.0) / vo.getDailyGoal());
            vo.setCompletionRate(Math.min(rate, 100));
        } else {
            vo.setCompletionRate(0);
        }
        
        // 检查是否有奖励可领取
        boolean hasClaimedToday = readingRewardRepository.existsByUserIdAndRewardDateAndRewardType(
                userId, today, "daily");
        vo.setHasRewardToClaim(!hasClaimedToday && todayReadingTime >= vo.getDailyGoal());
        
        return vo;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReadingTimeStatItemVO> getReadingStats(Integer userId, String type, Integer year) {
        List<ReadingTimeStatItemVO> stats = new ArrayList<>();
        
        LocalDate now = LocalDate.now();
        if (year == null) {
            year = now.getYear();
        }
        
        switch (type.toLowerCase()) {
            case "daily":
                // 获取当月每日数据
                stats = getDailyStats(userId, year, now.getMonthValue());
                break;
                
            case "monthly":
                // 获取当年每月数据
                stats = getMonthlyStats(userId, year);
                break;
                
            case "yearly":
                // 获取最近5年数据
                stats = getYearlyStats(userId);
                break;
                
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的统计类型");
        }
        
        return stats;
    }
    
    @Override
    @Transactional
    public boolean claimReadingReward(Integer userId) {
        LocalDate today = LocalDate.now();
        
        // 检查今日是否已领取
        boolean hasClaimed = readingRewardRepository.existsByUserIdAndRewardDateAndRewardType(
                userId, today, "daily");
        
        if (hasClaimed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "今日已领取过奖励");
        }
        
        // 检查今日阅读时长是否达标（30分钟）
        Integer todayReadingTime = readingRecordRepository.sumReadingTimeByUserIdAndDate(userId, today);
        if (todayReadingTime == null || todayReadingTime < 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "今日阅读时长未达标（需30分钟）");
        }
        
        // 创建奖励记录
        ReadingRewardEntity reward = new ReadingRewardEntity();
        reward.setUserId(userId);
        reward.setRewardDate(today);
        reward.setRewardType("daily");
        reward.setRewardValue(1); // 1天体验卡
        reward.setDescription("每日阅读激励奖励");
        reward.setIsClaimed(true);
        reward.setClaimedAt(LocalDateTime.now());
        readingRewardRepository.save(reward);
        
        // 创建会员卡（1天体验卡）
        MemberCardEntity card = new MemberCardEntity();
        card.setUserId(userId);
        card.setCardName("阅读激励体验卡");
        card.setDurationDays(1);
        card.setSourceType("reward");
        card.setSourceOrderNo("REWARD_" + System.currentTimeMillis());
        card.setStatus(MemberCardEntity.Status.VALID.getCode());
        card.setExpireAt(LocalDateTime.now().plusYears(1));
        memberCardRepository.save(card);
        
        return true;
    }
    
    @Override
    @Transactional
    public void addReadingRecord(Integer userId, Integer bookId, String bookTitle, 
                               Integer readingTime, Integer pageCount, Integer chapterId, 
                               String chapterTitle) {
        UserReadingRecordEntity record = new UserReadingRecordEntity();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBookTitle(bookTitle);
        record.setReadDate(LocalDate.now());
        record.setReadingTime(readingTime);
        record.setPageCount(pageCount);
        record.setChapterId(chapterId);
        record.setChapterTitle(chapterTitle);
        record.setRecordType(1); // 阅读记录
        
        readingRecordRepository.save(record);
        
        // 更新连续阅读天数（这里需要实现逻辑）
        updateConsecutiveReadingDays(userId);
    }
    
    // ============ 私有方法 ============
    
    private List<ReadingTimeStatItemVO> getDailyStats(Integer userId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // 查询月度数据
        List<Object[]> dailyData = readingRecordRepository.findDailyStatsByUserIdAndDateRange(
                userId, startDate, endDate);
        
        List<ReadingTimeStatItemVO> stats = new ArrayList<>();
        
        // 填充整个月的日期
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            ReadingTimeStatItemVO item = new ReadingTimeStatItemVO();
            item.setTime(dateStr);
            item.setReadingTime(0);
            
            // 查找是否有对应日期的数据
            for (Object[] data : dailyData) {
                LocalDate recordDate = (LocalDate) data[0];
                if (recordDate.equals(date)) {
                    item.setReadingTime(((Number) data[1]).intValue());
                    break;
                }
            }
            
            stats.add(item);
        }
        
        return stats;
    }
    
    private List<ReadingTimeStatItemVO> getMonthlyStats(Integer userId, Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        List<Object[]> monthlyData = readingRecordRepository.findMonthlyStatsByUserIdAndDateRange(
                userId, startDate, endDate);
        
        List<ReadingTimeStatItemVO> stats = new ArrayList<>();
        
        // 填充全年12个月
        for (int month = 1; month <= 12; month++) {
            String monthStr = String.format("%04d-%02d", year, month);
            
            ReadingTimeStatItemVO item = new ReadingTimeStatItemVO();
            item.setTime(monthStr);
            item.setReadingTime(0);
            
            // 查找是否有对应月份的数据
            for (Object[] data : monthlyData) {
                String recordMonth = (String) data[0];
                if (recordMonth.equals(monthStr)) {
                    item.setReadingTime(((Number) data[1]).intValue());
                    break;
                }
            }
            
            stats.add(item);
        }
        
        return stats;
    }
    
    private List<ReadingTimeStatItemVO> getYearlyStats(Integer userId) {
        int currentYear = LocalDate.now().getYear();
        List<ReadingTimeStatItemVO> stats = new ArrayList<>();
        
        // 获取最近5年数据
        for (int year = currentYear - 4; year <= currentYear; year++) {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);
            
            Integer yearlyTotal = readingRecordRepository.sumReadingTimeByUserIdAndDateRange(
                    userId, startDate, endDate);
            
            ReadingTimeStatItemVO item = new ReadingTimeStatItemVO();
            item.setTime(String.valueOf(year));
            item.setReadingTime(yearlyTotal != null ? yearlyTotal : 0);
            stats.add(item);
        }
        
        return stats;
    }
    
    private void updateConsecutiveReadingDays(Integer userId) {
        // 这里实现更新连续阅读天数的逻辑
        // 可以查询最近几天的阅读记录，计算连续天数
        log.debug("更新用户 {} 的连续阅读天数", userId);
    }
}