package com.weread.vo.community;

import lombok.Data;

@Data
public class TopicDetailRelatedVO {
    private String image;
    private String topicName;        
    private Integer postCount;
    private Integer topicId;
}