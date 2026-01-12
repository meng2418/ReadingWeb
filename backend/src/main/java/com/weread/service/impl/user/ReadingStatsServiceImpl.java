package com.weread.service.impl.user;

import com.weread.dto.reading.ReadingStatsDTO;
import com.weread.repository.user.UserReadingRecordRepository;
import com.weread.service.user.ReadingStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class ReadingStatsServiceImpl implements ReadingStatsService {

    @Autowired
    private UserReadingRecordRepository userReadingRecordRepository;

    @Override
    public ReadingStatsDTO getReadingStats(Integer userId) {
        ReadingStatsDTO stats = new ReadingStatsDTO();

        // 获取当前日期
        LocalDate today = LocalDate.now();

        // 获取本周的开始和结束时间（周一和周日）
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 获取本月的开始和结束时间
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        // 获取本年的开始和结束时间
        LocalDate startOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());

        // 查询各个时间段的阅读时长
        Integer weeklyReadingTime = userReadingRecordRepository
                .findWeeklyReadingTime(userId, startOfWeek, endOfWeek);
        Integer monthlyReadingTime = userReadingRecordRepository
                .findMonthlyReadingTime(userId, startOfMonth, endOfMonth);
        Integer yearlyReadingTime = userReadingRecordRepository
                .findYearlyReadingTime(userId, startOfYear, endOfYear);
        Integer totalReadingTime = userReadingRecordRepository
                .findTotalReadingTime(userId);

        // 设置DTO，如果为null则设为0
        stats.setWeeklyReadingTime(weeklyReadingTime != null ? weeklyReadingTime : 0);
        stats.setMonthlyReadingTime(monthlyReadingTime != null ? monthlyReadingTime : 0);
        stats.setYearlyReadingTime(yearlyReadingTime != null ? yearlyReadingTime : 0);
        stats.setTotalReadingTime(totalReadingTime != null ? totalReadingTime : 0);

        return stats;
    }

    /**
     * 可选扩展方法：获取最近7天的阅读数据
     */
    public Map<LocalDate, Integer> getLast7DaysReadingStats(Integer userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<Object[]> results = userReadingRecordRepository
                .findDailyReadingTimeByDateRange(userId, startDate);

        Map<LocalDate, Integer> stats = new LinkedHashMap<>();

        // 初始化7天的数据为0
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            stats.put(date, 0);
        }

        // 填充有数据的日期
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Integer time = ((Number) result[1]).intValue();
            stats.put(date, time);
        }

        return stats;
    }
}