package com.weread.vo.user;

import lombok.Data;

@Data
public class FollowUserVO {
    private Integer userId;
    private String username;
    private String avatar;
    private String bio;

    // 必需字段
    private Boolean isFollowing;
    private Boolean isFollower;

    // 确保在构造时设置默认值
    public FollowUserVO() {
        this.username = "";
        this.avatar = "";
        this.bio = "";
        this.isFollowing = false;
        this.isFollower = false;
    }
}