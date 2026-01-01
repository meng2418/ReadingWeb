package com.weread.vo.user;

import lombok.Data;

@Data
public class UserSimpleVO {

    private Integer userId;     // 对应 integer
    private String username;
    private String avatar;   // uri 在后端就是 String
    private String bio;
}
