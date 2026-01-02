package com.weread.vo.user;

import lombok.Data;

@Data
public class UserProfileVO {
    
    private String avatar;
    private String username;
    private String bio;
    private Integer followingCount;
    private Integer followerCount;
    private Integer postCount;
    private Integer memberCardCount;
    private Integer coinCount;
    private Boolean isMember;
    private Integer memberExpireDays;
    
    private ReadingStatsVO readingStats;
    private Integer consecutiveReadingDays;
    
    @Data
    public static class ReadingStatsVO {
        private Integer weeklyReadingTime;    // 周阅读时长（分钟）
        private Integer monthlyReadingTime;   // 月阅读时长（分钟）
        private Integer yearlyReadingTime;    // 年阅读时长（分钟）
        private Integer totalReadingTime;     // 总阅读时长（分钟）
        
        private Integer weeklyReadCount;      // 周读过的书数量
        private Integer monthlyReadCount;     // 月读过的书数量
        private Integer yearlyReadCount;      // 年读过的书数量
        private Integer totalReadCount;       // 总读过的书数量
        
        private Integer weeklyFinishedCount;  // 周读完的书数量
        private Integer monthlyFinishedCount; // 月读完的书数量
        private Integer yearlyFinishedCount;  // 年读完的书数量
        private Integer totalFinishedCount;   // 总读完的书数量
        
        private Integer weeklyNoteCount;      // 周笔记数量
        private Integer monthlyNoteCount;     // 月笔记数量
        private Integer yearlyNoteCount;      // 年笔记数量
        private Integer totalNoteCount;       // 总笔记数量
    }
}