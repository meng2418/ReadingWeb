package com.weread.dto.community;

import lombok.Data;

@Data
public class TopicSearchResultDTO {
    private Integer topicId;
    private String topicName;
    private Integer viewCount;
    private Integer discussionCount;
}