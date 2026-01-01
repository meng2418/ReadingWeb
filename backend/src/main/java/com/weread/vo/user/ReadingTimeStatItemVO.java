package com.weread.vo.user;

import lombok.Data;

@Data
public class ReadingTimeStatItemVO {
    
    private String time; // 时间标识：2024-01-15, 2024-01, 2024
    private Integer readingTime; // 阅读时长（分钟）
}