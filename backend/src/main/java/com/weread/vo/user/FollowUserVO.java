package com.weread.vo.user;

import lombok.Data;

@Data
public class FollowUserVO {
    private Long userId;
    private String username;
    private String avatarUrl;

    /**
     * true: 当前登录用户已关注此列表中的用户
     */
    private boolean isFollowedByMe;

    /**
     * true: 此列表中的用户是否关注了当前登录用户（即是否互粉）
     */
    private boolean isMutualFollow;
}