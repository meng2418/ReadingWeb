package com.weread.service.user;

import com.weread.vo.user.UserProfileOtherVO;

public interface UserProfileService {
    UserProfileOtherVO getUserProfile(Integer targetUserId, Integer currentUserId, Integer page, Integer limit);
}
