package com.weread.vo.user;

import lombok.Data;

@Data
public class UserSummaryVO {
    private Integer userId;
    private String username;
    private String avatarUrl; // 用户头像URL
}