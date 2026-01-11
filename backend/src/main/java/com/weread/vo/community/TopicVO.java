package com.weread.vo.community;

import lombok.Data;

/**
 * 话题展示值对象
 */
@Data
public class TopicVO {

    private Integer topicId;
    private String topicName;
    private String image;
    private Integer postCount; 

    public String getImage() {
        return image != null ? image : "";
    }
    
}