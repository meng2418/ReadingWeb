package com.weread.service.impl.user;

import com.weread.dto.user.UpdateProfileDTO;
import com.weread.entity.user.FollowEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.UserRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.service.user.UserService;
import com.weread.vo.user.FollowListVO;
import com.weread.vo.user.FollowUserVO;
import com.weread.vo.user.UserProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    @Override
    @Transactional(readOnly = true)
    public UserProfileVO getUserHome(Integer userId) {
        // 获取用户基本信息
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        
        // 构建UserProfileVO
        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setAvatar(user.getAvatar());
        profileVO.setUsername(user.getUsername());
        profileVO.setBio(user.getBio());
        profileVO.setCoinCount(user.getCoins() != null ? user.getCoins() : 0);
        
        // 会员状态
        profileVO.setIsMember(user.getMembershipExpireAt() != null && 
                             ((LocalDateTime) user.getMembershipExpireAt()).isAfter(LocalDateTime.now()));
        
        // 计算会员剩余天数
        if (user.getMembershipExpireAt() != null) {
            long days = ChronoUnit.DAYS.between(LocalDateTime.now(), (Temporal) user.getMembershipExpireAt());
            profileVO.setMemberExpireDays(days > 0 ? (int) days : 0);
        } else {
            profileVO.setMemberExpireDays(0);
        }
        
        // 统计数据（这里需要调用其他服务或Repository获取）
        profileVO.setFollowingCount(getFollowingCount(userId));
        profileVO.setFollowerCount(getFollowerCount(userId));
        profileVO.setPostCount(getPostCount(userId));
        profileVO.setMemberCardCount(getMemberCardCount(userId));
        
        // 阅读统计
        profileVO.setReadingStats(getReadingStats(userId));
        profileVO.setConsecutiveReadingDays(getConsecutiveReadingDays(userId));
        
        return profileVO;
    }
    
    @Override
    @Transactional
    public UserProfileVO updateUserProfile(Integer userId, UpdateProfileDTO updateDTO) {
        // 获取用户
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        
        // 检查用户名是否已被占用（如果用户名有修改）
        if (updateDTO.getUsername() != null && 
            !updateDTO.getUsername().equals(user.getUsername())) {
            boolean exists = userRepository.existsByUsername(updateDTO.getUsername());
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名已被占用");
            }
            user.setUsername(updateDTO.getUsername());
        }
        
        // 更新其他字段
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        
        if (updateDTO.getBio() != null) {
            user.setBio(updateDTO.getBio());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // 返回更新后的信息
        return getUserHome(userId);
    }
    
    @Override
    @Transactional
    public void updateLastLoginTime(Integer userId) {
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
    }
    
    // ============ 私有方法，需要根据实际情况实现 ============
    
    private Integer getFollowingCount(Integer userId) {
        return followRepository.countByFollowerId(userId);
    }
    
    private Integer getFollowerCount(Integer userId) {
        return followRepository.countByFollowingId(userId);
    }
    
    private Integer getPostCount(Integer userId) {
        return postRepository.countByAuthorId(userId);
    }
    
    private Integer getMemberCardCount(Integer userId) {
        // 这里需要调用会员卡相关的Repository
        return 0; // 示例
    }
    
    private UserProfileVO.ReadingStatsVO getReadingStats(Integer userId) {
        // 这里需要调用阅读统计相关的Repository
        UserProfileVO.ReadingStatsVO stats = new UserProfileVO.ReadingStatsVO();
        
        // 示例数据，实际应从数据库获取
        stats.setWeeklyReadingTime(120);
        stats.setMonthlyReadingTime(480);
        stats.setYearlyReadingTime(5760);
        stats.setTotalReadingTime(11520);
        
        stats.setWeeklyReadCount(3);
        stats.setMonthlyReadCount(12);
        stats.setYearlyReadCount(48);
        stats.setTotalReadCount(96);
        
        stats.setWeeklyFinishedCount(1);
        stats.setMonthlyFinishedCount(4);
        stats.setYearlyFinishedCount(16);
        stats.setTotalFinishedCount(32);
        
        stats.setWeeklyNoteCount(5);
        stats.setMonthlyNoteCount(20);
        stats.setYearlyNoteCount(240);
        stats.setTotalNoteCount(480);
        
        return stats;
    }
    
    private Integer getConsecutiveReadingDays(Integer userId) {
        // 这里需要调用阅读记录相关的Repository
        // 计算连续阅读天数
        return 7; // 示例
    }

    @Override
    @Transactional
    public void followUser(Integer followerId, Integer followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能关注自己。");
        }
        
        // 1. 验证目标用户是否存在
        if (!userRepository.existsById(followingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "目标用户不存在。");
        }

        // 2. 检查是否已关注
        if (followRepository.findByFollowerIdAndFollowingId(followerId, followingId).isPresent()) {
            return; // 已关注，无需操作
        }

        // 3. 保存新的 FollowEntity
        FollowEntity follow = new FollowEntity();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        followRepository.save(follow);
        
        // 【✅ 实际更新操作】: 更新 UserEntity 中的 followingCount/followerCount 计数
        userRepository.incrementFollowingCount(followerId); // 关注者 (follower) 的关注数 +1
        userRepository.incrementFollowerCount(followingId); // 被关注者 (following) 的粉丝数 +1
    }
    // =========================================================
    // 【实现 2】: 取消关注用户
    // =========================================================

    @Override
    @Transactional // 必须要有 @Transactional
    public void unfollowUser(Integer followerId, Integer followingId) {
    
        // 1. 查找关注记录
        Optional<FollowEntity> existingFollow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
    
        if (existingFollow.isPresent()) {
            // 2. 删除关注记录
            followRepository.delete(existingFollow.get());
        
            // 【✅ 实际更新操作】: 更新 UserEntity 中的 followingCount/followerCount 计数
        
            // 3. 关注者 (follower) 的关注数 -1
            userRepository.decrementFollowingCount(followerId); 
        
            // 4. 被关注者 (following) 的粉丝数 -1
            userRepository.decrementFollowerCount(followingId);
        }
        // 如果不存在，方法结束，无需操作
    }
    @Override
    public FollowListVO getFollowers(Integer userId, int page, int limit, Integer currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 查找目标用户（userId）的粉丝列表 (即 FollowingId = userId)
        Page<FollowEntity> followPage = followRepository.findByFollowingId(userId, pageable);
        
        return buildFollowListResponse(followPage, currentUserId, true);
    }

    @Override
    public FollowListVO getFollowings(Integer userId, int page, int limit, Integer currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 查找目标用户（userId）的关注列表 (即 FollowerId = userId)
        Page<FollowEntity> followPage = followRepository.findByFollowerId(userId, pageable);
        
        return buildFollowListResponse(followPage, currentUserId, false);
    }



    /**
     * 辅助方法：将 FollowEntity 列表转换为 FollowUserVO 列表
     * @param followPage 包含 FollowEntity 的分页结果
     * @param currentUserId 当前登录用户ID (用于判断是否互相关注)
     * @param isFollowersList true表示正在查询粉丝列表 (FollowingId是目标用户)，false表示查询关注列表 (FollowerId是目标用户)
     */
    private FollowListVO buildFollowListResponse(Page<FollowEntity> followPage, Integer currentUserId, boolean isFollowersList) {
        
        List<Integer> targetUserIds = followPage.getContent().stream().map(entity -> isFollowersList ? entity.getFollowerId() : entity.getFollowingId()).toList();

        // 1. 批量查询目标用户信息
        List<UserEntity> targetUsers = userRepository.findAllById(targetUserIds);
        
        // 2. 批量查询当前用户与列表中用户的关注关系 (用于判断是否互相关注)
        List<FollowUserVO> followUserVOs = targetUsers.stream()
            .map(userEntity -> {
                FollowUserVO vo = convertToFollowUserVO(userEntity);
                
                if (currentUserId != null) {
                    // a. 检查当前用户是否关注了目标用户 (isFollowedByMe)
                    vo.setFollowedByMe(followRepository.findByFollowerIdAndFollowingId(currentUserId, userEntity.getUserId()).isPresent());
                    
                    // b. 检查目标用户是否关注了当前用户 (isMutualFollow)
                    // 只有在查询粉丝列表时，这才是"互关"的判断逻辑
                    vo.setMutualFollow(followRepository.findByFollowerIdAndFollowingId(userEntity.getUserId(), currentUserId).isPresent());
                } else {
                    vo.setFollowedByMe(false);
                    vo.setMutualFollow(false);
                }
                return vo;
            })
            .toList();

        FollowListVO response = new FollowListVO();
        response.setUsers(followUserVOs);
        response.setTotalElements(followPage.getTotalElements());
        response.setTotalPages(followPage.getTotalPages());
        return response;
    }

    /**
     * 辅助方法：将 UserEntity 转换为 FollowUserVO
     */
    private FollowUserVO convertToFollowUserVO(UserEntity userEntity) {
        FollowUserVO vo = new FollowUserVO();
        vo.setUserId(userEntity.getUserId());
        vo.setUsername(userEntity.getUsername());
        vo.setAvatarUrl(userEntity.getAvatar()); 
        return vo;
    }
}