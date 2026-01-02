package com.weread.vo.user;

import lombok.Data;
@Data
public class TodayReadingTimeVO {
    
    private Integer readingTime; // 今日阅读时长（分钟）
    private Integer dailyGoal = 30; // 每日目标（分钟）
    private Integer completionRate; // 完成率（百分比）
    private Boolean hasRewardToClaim; // 是否有奖励可领取
}