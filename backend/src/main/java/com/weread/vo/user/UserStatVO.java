package com.weread.vo.user;

import com.weread.entity.user.UserStatEntity;
import lombok.Builder;
import lombok.Data;

/**
 * 用户统计数据展示值对象 (Value Object)
 * 用于向前端展示用户的累计阅读数据和成就数据。
 */
@Data
@Builder
public class UserStatVO {

    private Long userId;

    /**
     * 累计阅读书籍数量
     */
    private Integer totalBooksCompleted;
    
    /**
     * 累计笔记数量
     */
    private Integer totalNotesCount;
    
    /**
     * 累计阅读时长 (单位：小时，由分钟转换而来)
     */
    private Double totalReadingDurationHours; 

    /**
     * 静态方法：将 Entity 转换为 VO
     * 负责数据清洗和单位转换（分钟 -> 小时）。
     * * @param entity 数据库中的 UserStatEntity
     * @return UserStatVO
     */
    public static UserStatVO fromEntity(UserStatEntity entity) {
        
        // 将分钟转换为小时
        double minutes = entity.getTotalReadingDurationMinutes();
        double hours = minutes / 60.0;
        
        return UserStatVO.builder()
                .userId(entity.getUserId())
                .totalBooksCompleted(entity.getTotalBooksCompleted())
                .totalNotesCount(entity.getTotalNotesCount())
                // 保留两位小数，确保前端展示整洁
                .totalReadingDurationHours(Math.round(hours * 100.0) / 100.0) 
                .build();
    }
}