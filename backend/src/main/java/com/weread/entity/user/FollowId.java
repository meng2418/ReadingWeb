package com.weread.entity.user;

import lombok.Data;
import jakarta.persistence.Embeddable;

@Embeddable
@Data
public class FollowId { // 放在 FollowId.java 文件中，所以可以是 public
    private Long followerId;  
    private Long followedId;  
}