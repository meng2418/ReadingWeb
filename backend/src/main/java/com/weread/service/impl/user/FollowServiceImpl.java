package com.weread.service.impl.user;

import com.weread.dto.response.user.FollowResultResponse;
import com.weread.entity.user.FollowEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.FollowRepository;
import com.weread.repository.user.UserRepository;
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
    private final UserRepository userRepository;

    public FollowServiceImpl(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
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
    @Transactional
    public FollowResultResponse followUser(Integer loginUserId, Integer targetUserId) {
        // 检查是否已关注
        if (followRepository.findByFollowerIdAndFollowingId(loginUserId, targetUserId).isEmpty()) {
            // 不能关注自己
            if (loginUserId.equals(targetUserId)) {
                throw new IllegalArgumentException("不能关注自己");
            }
            
            // 验证目标用户是否存在
            if (!userRepository.existsById(targetUserId)) {
                throw new IllegalArgumentException("目标用户不存在");
            }
            
            // 保存关注关系
            FollowEntity entity = new FollowEntity();
            entity.setFollowerId(loginUserId);
            entity.setFollowingId(targetUserId);
            followRepository.save(entity);
            
            // 更新用户计数
            userRepository.incrementFollowingCount(loginUserId); // 关注者的关注数 +1
            userRepository.incrementFollowerCount(targetUserId); // 被关注者的粉丝数 +1
        }
        return getFollowResult(loginUserId, targetUserId);
    }

    @Override
    @Transactional 
    public FollowResultResponse unfollowUser(Integer loginUserId, Integer targetUserId) {
        // 检查是否存在关注关系
        if (followRepository.findByFollowerIdAndFollowingId(loginUserId, targetUserId).isPresent()) {
            // 删除关注关系
            followRepository.deleteByFollowerIdAndFollowingId(loginUserId, targetUserId);
            
            // 更新用户计数
            userRepository.decrementFollowingCount(loginUserId); // 关注者的关注数 -1
            userRepository.decrementFollowerCount(targetUserId); // 被关注者的粉丝数 -1
        }
        return getFollowResult(loginUserId, targetUserId);
    }


    @Override
    public List<UserWithFollowVO> getFollowers(Integer userId, Integer cursor, int limit) {
        // 这里可以用 Page + Pageable 或自定义查询类似 getFollowingUsers
        Page<FollowEntity> page = followRepository.findByFollowingId(userId, PageRequest.of(0, limit));
        List<FollowEntity> followers = page.getContent();

        return followers.stream()
            .map(f -> {
                UserEntity user = userRepository.findById(f.getFollowerId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + f.getFollowerId()));
                UserWithFollowVO vo = new UserWithFollowVO();
                vo.setUserId(user.getUserId());
                vo.setUsername(user.getUsername() != null ? user.getUsername() : "");
                vo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
                vo.setBio(user.getBio() != null ? user.getBio() : "");
                // 检查当前登录用户是否关注了这个粉丝
                // 注意：这里需要传入当前登录用户ID，但方法签名中没有，暂时设为false
                vo.setIsFollowing(false);
                vo.setIsFollower(true); // 这是粉丝，所以 isFollower 为 true
                return vo;
            })
            .collect(Collectors.toList());
    }

    @Override
    public FollowResultResponse getFollowResult(Integer loginUserId, Integer targetUserId) {
        boolean isFollowing = followRepository.findByFollowerIdAndFollowingId(loginUserId, targetUserId).isPresent();

        // 获取当前用户的关注数
        Integer followingCount = userRepository.findByUserId(loginUserId)
            .map(UserEntity::getFollowingCount)
            .orElse(0);
        
        // 获取目标用户的粉丝数
        Integer followerCount = userRepository.findByUserId(targetUserId)
            .map(UserEntity::getFollowerCount)
            .orElse(0);

        FollowResultResponse resp = new FollowResultResponse();
        resp.setFollowing(isFollowing);
        resp.setFollowingCount(followingCount);
        resp.setFollowerCount(followerCount);
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
