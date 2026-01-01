package com.weread.service.user;

import com.weread.vo.user.ReadingTimeStatItemVO;
import com.weread.vo.user.TodayReadingTimeVO;
import java.util.List;

public interface ReadingService {
    
    /**
     * 获取今日阅读时长
     */
    TodayReadingTimeVO getTodayReadingTime(Integer userId);
    
    /**
     * 获取阅读统计数据
     */
    List<ReadingTimeStatItemVO> getReadingStats(Integer userId, String type, Integer year);
    
    /**
     * 领取阅读激励
     */
    boolean claimReadingReward(Integer userId);
    
    /**
     * 添加阅读记录
     */
    void addReadingRecord(Integer userId, Integer bookId, String bookTitle, 
                         Integer readingTime, Integer pageCount, Integer chapterId, 
                         String chapterTitle);
}