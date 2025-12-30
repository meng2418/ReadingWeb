package com.weread.service.impl.user;

import com.weread.entity.user.UserEntity;
import com.weread.repository.user.FollowRepository;
import com.weread.service.user.FollowService;
import com.weread.vo.user.UserWithFollowVO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    public FollowServiceImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public List<UserWithFollowVO> getFollowingUsers(Integer userId, String cursor, int limit) {
        // 从数据库查询关注关系，按时间或ID降序排序
        List<UserEntity> following = followRepository.findFollowing(userId, cursor, limit);

        return following.stream().map(user -> {
            UserWithFollowVO vo = new UserWithFollowVO();
            vo.setUserId(user.getUserId());
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
            vo.setBio(user.getBio());
            return vo;
        }).collect(Collectors.toList());
    }
}
