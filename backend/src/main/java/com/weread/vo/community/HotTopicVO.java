package com.weread.vo.community;

import lombok.Data;

@Data
public class HotTopicVO {
    private Integer topicId;
    private String topicName;  // 接口只返回话题名称字段
}
