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
    private Integer experienceCardCount;
    private Integer coinCount;
    private Boolean isMember;
    private Integer memberExpireDays;
    
    private ReadingStatsVO readingStats;
    private Integer consecutiveReadingDays;
    
    @Data
    public static class ReadingStatsVO {
        private Integer weeklyReadingTime = 0;    // 周阅读时长（分钟）
        private Integer monthlyReadingTime = 0;   // 月阅读时长（分钟）
        private Integer yearlyReadingTime = 0;    // 年阅读时长（分钟）
        private Integer totalReadingTime = 0;     // 总阅读时长（分钟）
        
        private Integer weeklyBooksRead = 0;      // 周读过的书数量
        private Integer monthlyBooksRead = 0;     // 月读过的书数量
        private Integer yearlyBooksRead = 0;      // 年读过的书数量
        private Integer totalBooksRead = 0;       // 总读过的书数量
        
        private Integer weeklyBooksFinished = 0;  // 周读完的书数量
        private Integer monthlyBooksFinished = 0; // 月读完的书数量
        private Integer yearlyBooksFinished = 0;  // 年读完的书数量
        private Integer totalBooksFinished = 0;   // 总读完的书数量
        
        private Integer weeklyNoteCount = 0;      // 周笔记数量
        private Integer monthlyNoteCount = 0;     // 月笔记数量
        private Integer yearlyNoteCount = 0;      // 年笔记数量
        private Integer totalNoteCount = 0;       // 总笔记数量
    }
}