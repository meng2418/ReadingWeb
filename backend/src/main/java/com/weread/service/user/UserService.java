package com.weread.service.user;

import com.weread.dto.user.ProfileUpdateDTO;
import com.weread.dto.user.PasswordUpdateDTO;
import com.weread.vo.user.LoginLogVO;
import com.weread.vo.user.FollowListVO;
import com.weread.vo.user.UserDetailVO;

import java.util.List;
// 注意：PhoneCodeSendDTO, BindPhoneDTO, UnbindPhoneDTO 仍应存在于项目中

/**
 * UserService - 仅负责核心用户身份和账号安全管理。
 */
public interface UserService {
    // ===========================================
    // 1. 个人信息 (Profile Management)
    // ===========================================
    UserDetailVO getUserProfile(Long userId);
    void updateProfile(Long userId, ProfileUpdateDTO request);
    void updatePassword(Long userId, PasswordUpdateDTO request);

    // ===========================================
    // 2. 账号安全 (Account Security)
    // ===========================================
    // 假设您还需要 phone 相关的 send/bind/unbind 方法

    /** 分页查询用户的登录历史记录 */
    List<LoginLogVO> getLoginLogs(Long userId, int page, int size);
    
    // 原来的 3. 会员体系 和 4. 我的资产 部分已移除

    /**
     * 执行关注操作
     * @param followerId 关注者 ID (当前用户)
     * @param followingId 被关注者 ID (目标用户)
     */
    void followUser(Long followerId, Long followingId);

    /**
     * 执行取消关注操作
     * @param followerId 关注者 ID (当前用户)
     * @param followingId 被关注者 ID (目标用户)
     */
    void unfollowUser(Long followerId, Long followingId);

    /**
     * 获取用户的粉丝列表 (带分页)
     * @param userId 目标用户ID
     * @param page 页码
     * @param limit 限制数量
     * @param currentUserId 当前用户ID (用于检查是否互相关注)
     */
    FollowListVO getFollowers(Long userId, int page, int limit, Long currentUserId);

    /**
     * 获取用户的关注列表 (带分页)
     * @param userId 目标用户ID
     * @param page 页码
     * @param limit 限制数量
     * @param currentUserId 当前用户ID (用于检查是否互相关注)
     */
    FollowListVO getFollowings(Long userId, int page, int limit, Long currentUserId);
}