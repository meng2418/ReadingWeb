package com.weread.service.user;

import com.weread.dto.reading.ReadingStatsDTO;

public interface ReadingStatsService {
    /**
     * 获取用户的阅读统计
     * 
     * @param userId 用户ID
     * @return 阅读统计数据
     */
    ReadingStatsDTO getReadingStats(Long userId);
}
