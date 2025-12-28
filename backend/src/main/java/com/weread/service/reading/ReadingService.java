package com.weread.service.reading;

import com.weread.dto.reading.ReadingRecordDTO;
import com.weread.vo.reading.ReadingStatVO;

/**
 * 阅读服务接口
 */
public interface ReadingService {

    /**
     * 记录阅读进度
     */
    void recordReadingProgress(Long userId, ReadingRecordDTO dto);

    /**
     * 获取阅读统计
     */
    ReadingStatVO getReadingStatistics(Long userId);

    /**
     * 记录阅读时长（分钟）
     */
    void recordReadingDuration(Long userId, Integer minutes);
}

