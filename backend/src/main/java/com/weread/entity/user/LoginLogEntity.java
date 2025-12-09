package com.weread.entity.user; 
// 假设放在基础实体包中

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "login_log_info")
public class LoginLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginLogId;

    private Long userId; // 关联的用户ID
    
    private LocalDateTime loginTime; // 登录时间
    
    @Column(length = 64)
    private String ipAddress; // 登录IP地址

    private String loginLocation; // 登录地点
    
    @Column(length = 255)
    private String device; // 设备信息 (User-Agent)
    
    @Column(length = 20)
    private String status; // 登录状态 (SUCCESS, FAILED)
    
    @Column(length = 500)
    private String remarks; // 备注/失败原因
}