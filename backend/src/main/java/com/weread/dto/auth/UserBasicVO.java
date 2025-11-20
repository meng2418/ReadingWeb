package com.weread.dto.auth;

import lombok.Data;

@Data
public class UserBasicVO {
    private Long userId;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    // 可以添加其他关键信息，如是否已实名认证等
}
