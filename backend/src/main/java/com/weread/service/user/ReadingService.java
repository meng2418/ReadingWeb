package com.weread.service.user;

import com.weread.vo.user.MilestoneVO;
import com.weread.vo.user.ReadingTimeStatItemVO;
import com.weread.vo.user.TodayReadingTimeVO;
import com.weread.vo.user.TopBooksVO;

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

    /**
     * 获取用户特定时间段阅读最久的3本书
     */
    TopBooksVO getTopBooksByPeriod(Integer userId, String period);
    
    /**
     * 获取用户最新里程碑
     */
    MilestoneVO getLatestMilestones(Integer userId);
    
    /**
     * 检查并更新用户里程碑
     */
    void checkAndUpdateMilestones(Integer userId, String actionType, Object data);
}