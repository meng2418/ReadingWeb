package com.weread.vo.reading;

import lombok.Data;

/**
 * 阅读统计VO
 */
@Data
public class ReadingStatVO {

    private Long userId;

    private Integer todayReadingMinutes; // 今日阅读时长（分钟）

    private Integer totalReadingMinutes; // 总阅读时长（分钟）

    private Integer consecutiveDays; // 连续阅读天数

    private Integer totalBooksRead; // 总阅读书籍数
}

