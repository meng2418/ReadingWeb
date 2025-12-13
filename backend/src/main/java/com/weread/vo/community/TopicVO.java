package com.weread.vo.community;

import com.weread.entity.community.TopicEntity;
import lombok.Builder;
import lombok.Data;

/**
 * 话题展示值对象
 */
@Data
@Builder
public class TopicVO {

    private Integer topicId;
    private String name;
    private String description;
    
    // 话题下帖子数量（可选，如果需要统计）
    // private Integer postCount; 
    
    /**
     * 静态方法：将 Entity 转换为 VO
     * @param entity TopicEntity
     * @return TopicVO
     */
    public static TopicVO fromEntity(TopicEntity entity) {
        return TopicVO.builder()
                .topicId(entity.getTopicId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}