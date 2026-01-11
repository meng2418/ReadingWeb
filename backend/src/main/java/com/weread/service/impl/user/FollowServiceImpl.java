package com.weread.service.impl.user;

import com.weread.dto.response.user.FollowResultResponse;
import com.weread.entity.user.FollowEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.FollowRepository;
import com.weread.service.user.FollowService;
import com.weread.vo.user.UserWithFollowVO;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
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
    @Transactional
    public List<UserWithFollowVO> getFollowingUsers(Integer userId, Integer cursor, int limit) {
        // 从数据库查询关注关系，按时间或ID降序排序
        List<UserEntity> following = followRepository.findFollowing(userId, cursor, limit);

        return following.stream().map(user -> {
            UserWithFollowVO vo = new UserWithFollowVO();
            vo.setUserId(user.getUserId());
            vo.setUsername(user.getUsername() != null ? user.getUsername() : "");
            vo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
            vo.setBio(user.getBio() != null ? user.getBio() : "");
        
            // 设置默认值
            vo.setIsFollowing(true); // 因为是关注列表，所以isFollowing应该是true
            vo.setIsFollower(false); // 默认false
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public FollowResultResponse followUser(Integer loginUserId, Integer targetUserId) {
        if (followRepository.findByFollowerIdAndFollowingId(loginUserId, targetUserId).isEmpty()) {
            FollowEntity entity = new FollowEntity();
            entity.setFollowerId(loginUserId);
            entity.setFollowingId(targetUserId);
            followRepository.save(entity);
        }
        return getFollowResult(loginUserId, targetUserId);
    }

    @Override
    @Transactional 
    public FollowResultResponse unfollowUser(Integer loginUserId, Integer targetUserId) {
        followRepository.deleteByFollowerIdAndFollowingId(loginUserId, targetUserId);
        return getFollowResult(loginUserId, targetUserId);
    }


    @Override
    public List<UserWithFollowVO> getFollowers(Integer userId, Integer cursor, int limit) {
        // 这里可以用 Page + Pageable 或自定义查询类似 getFollowingUsers
        Page<FollowEntity> page = followRepository.findByFollowingId(userId, PageRequest.of(0, limit));
        List<FollowEntity> followers = page.stream().map(f -> followRepository.getOne(f.getFollowerId())).toList();

        return followers.stream().map(user -> {
            UserWithFollowVO vo = new UserWithFollowVO();
            vo.setUserId(user.getFollowerId());
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
            vo.setBio(user.getBio());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public FollowResultResponse getFollowResult(Integer loginUserId, Integer targetUserId) {
        boolean isFollowing = followRepository.findByFollowerIdAndFollowingId(loginUserId, targetUserId).isPresent();

        FollowResultResponse resp = new FollowResultResponse();
        resp.setFollowing(isFollowing);
        return resp;
    }

    @Override
    public boolean isFollowing(Integer currentUserId, Integer userId) {
        // 参数检查
        if (currentUserId == null || userId == null) {
            return false;
        }
        
        // 不能关注自己
        if (currentUserId.equals(userId)) {
            return false;
        }
        
        // 调用 Repository 检查是否关注
        return followRepository.existsByFollowerIdAndFollowingId(currentUserId, userId);
    }
}
