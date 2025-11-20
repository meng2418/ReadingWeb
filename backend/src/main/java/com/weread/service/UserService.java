package com.weread.service;

import java.util.List;
import com.weread.dto.user.UserDetailVO;
import com.weread.dto.user.UpdateProfileDTO;
import com.weread.dto.user.UpdatePasswordDTO;
import com.weread.dto.user.LoginLogVO;
import com.weread.dto.auth.EmailCodeSendDTO;
import com.weread.dto.auth.BindEmailDTO;
import com.weread.dto.auth.UnbindEmailDTO;
// 注意：PhoneCodeSendDTO, BindPhoneDTO, UnbindPhoneDTO 仍应存在于项目中

/**
 * UserService - 仅负责核心用户身份和账号安全管理。
 */
public interface UserService {
    // ===========================================
    // 1. 个人信息 (Profile Management)
    // ===========================================
    UserDetailVO getUserProfile(Long userId);
    void updateProfile(Long userId, UpdateProfileDTO request);
    void updatePassword(Long userId, UpdatePasswordDTO request);

    // ===========================================
    // 2. 账号安全 (Account Security)
    // ===========================================
    // 假设您还需要 phone 相关的 send/bind/unbind 方法
    
    /** 发送绑定邮箱的验证码 */
    void sendEmailBindCode(EmailCodeSendDTO request);

    /** 绑定邮箱，需验证码 */
    void bindEmail(Long userId, BindEmailDTO request);

    /** 解绑手机号或邮箱，需验证码 */
    void unbindEmail(Long userId, UnbindEmailDTO request);

    /** 分页查询用户的登录历史记录 */
    List<LoginLogVO> getLoginLogs(Long userId, int page, int size);
    
    // 原来的 3. 会员体系 和 4. 我的资产 部分已移除
}