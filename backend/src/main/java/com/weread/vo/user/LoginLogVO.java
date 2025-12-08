package com.weread.vo.user;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * LoginLogVO - 登录历史记录视图对象
 * 用于将数据库中的 LoginLogEntity 转换后返回给客户端。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogVO {
    /** 登录记录的唯一ID (可选，但推荐) */
    private Long loginLogId;

    /** 登录时间 */
    private LocalDateTime loginTime;

    /** 登录IP地址 */
    private String ipAddress;

    /** 登录地址 */
    private String loginLocation;

    /** 登录时的设备/客户端信息 (例如: Chrome on Windows, iOS App 1.0) */
    private String device;
    
    /** 登录结果 (可选: 成功/失败) */
    private String status;
}
