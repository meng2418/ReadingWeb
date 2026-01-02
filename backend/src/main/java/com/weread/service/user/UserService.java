package com.weread.service.user;

import com.weread.dto.user.UpdateProfileDTO;
import com.weread.vo.user.FollowListVO;
import com.weread.vo.user.UserProfileVO;

public interface UserService {
    
    /**
     * 获取用户个人中心信息
     * @param userId 用户ID
     * @return 用户个人中心信息
     */
    UserProfileVO getUserHome(Integer userId);
    
    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param updateDTO 更新信息
     * @return 更新后的用户信息
     */
    UserProfileVO updateUserProfile(Integer userId, UpdateProfileDTO updateDTO);
    
    /**
     * 更新用户上次登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(Integer userId);

    void unfollowUser(Integer followerId, Integer followingId);

    void followUser(Integer followerId, Integer followingId);

    FollowListVO getFollowers(Integer userId, int page, int limit, Integer currentUserId);

    FollowListVO getFollowings(Integer userId, int page, int limit, Integer currentUserId);
}