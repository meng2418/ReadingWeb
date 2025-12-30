package com.weread.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * UserDetailVO - 用户详细信息视图对象
 * 用于个人中心页面展示用户所有公开和私密属性。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailVO {

    /** 用户ID */
    private Integer userId;

    /** 用户名 (唯一标识，可能用于登录) */
    private String username;

    /** 头像URL */
    private String avatar;

    /** 个人简介/签名 */
    private String bio;

    /** 绑定的手机号 (可能部分脱敏，如 138****1234) */
    private String phone;


    /** 注册时间 */
    private LocalDateTime createdAt;
    
    // ===============================================
    // 注意：以下字段可能需要通过其他 Service/Repository 注入
    // 如果您将资产和会员逻辑独立，则这些信息应通过 AssetController/Service 获取。
    // 为了方便，这里作为可选字段保留。
    // ===============================================
    
    /** 是否为会员 */
    private Boolean isMember; 

    /** 会员到期时间 */
    private LocalDateTime memberEndDate;
    
    /** 当前书币余额 */
    private Integer coins;
}