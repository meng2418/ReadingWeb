package com.weread.service.user;

import com.weread.vo.user.UserWithFollowVO;
import java.util.List;

public interface FollowService {
    /**
     * 获取用户关注列表，支持分页 cursor
     * @param userId 当前登录用户ID
     * @param cursor 游标（第一次请求可传 null）
     * @param limit 最大返回条数
     * @return 关注用户列表
     */
    List<UserWithFollowVO> getFollowingUsers(Integer userId, String cursor, int limit);
}
