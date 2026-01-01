package com.weread.service;

import com.weread.dto.auth.LoginDTO;
import com.weread.dto.auth.LoginResultVO;
import com.weread.dto.auth.RegisterDTO;
import com.weread.entity.user.UserEntity;
// 导入我们新定义的 DTO
import com.weread.dto.auth.ResetPasswordDTO; 

public interface AuthService {
    
    // 登录/注册方法
    LoginResultVO phoneRegister(RegisterDTO dto);
    LoginResultVO phonePasswordLogin(LoginDTO dto);
    LoginResultVO smsLogin(LoginDTO dto);
    
    // 短信发送方法（通用）
    void sendSms(String phone); 

    // 发送重置密码短信验证码
    void sendForgetPasswordSms(String phoneNumber); 

    // 校验验证码并重置密码
    void resetPasswordBySms(ResetPasswordDTO dto); 
    // 注意：我们将重载为接收 DTO，以方便 Controller 调用
}