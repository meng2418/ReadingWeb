package com.weread.service.impl.user;


import com.weread.entity.user.FollowEntity;
import com.weread.entity.user.LoginLogEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.UserRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.repository.user.LoginLogRepository;
import com.weread.service.user.UserService;
import com.weread.service.util.GeoLocationService;
import com.weread.dto.user.ProfileUpdateDTO;
import com.weread.dto.user.PasswordUpdateDTO;
import com.weread.vo.user.LoginLogVO;
import com.weread.vo.user.UserDetailVO;
import com.weread.vo.user.FollowUserVO;
import com.weread.vo.user.FollowListVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.weread.util.RedisService; // 假设的Redis工具
import java.util.List;
import java.util.Optional;

// 假设的自定义异常
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
class InvalidCodeException extends RuntimeException {
    public InvalidCodeException(String message) { super(message); }
}


@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class); // 引入 Logger 替代 System.err

    private final LoginLogRepository loginLogRepository;
    private final GeoLocationService geoLocationService; // 【✅ 注入 GeoLocationService】
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    // 假设您还需要 Phone 相关的 DTO/VOs 和对应的逻辑，这里只展示 Email 部分。

    public UserServiceImpl(UserRepository userRepository, FollowRepository followRepository, PasswordEncoder passwordEncoder, RedisService redisService, LoginLogRepository loginLogRepository, 
                           GeoLocationService geoLocationService) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.loginLogRepository = loginLogRepository;
        this.geoLocationService = geoLocationService;
    }

    // ===========================================
    // 1. 个人信息 (Profile Management)
    // ===========================================

    @Override
    public UserDetailVO getUserProfile(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("用户未找到"));
        
        // 实际应用中，这里需要将 UserEntity 转换为 UserDetailVO
        UserDetailVO response = new UserDetailVO();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        // 注意：isMember 和 coins 等字段已不再由 UserService 负责
        
        return response;
    }

    @Override
    public void updateProfile(Long userId, ProfileUpdateDTO request) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("用户未找到"));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        userRepository.save(user);
    }

    @Override
    public void updatePassword(Long userId, PasswordUpdateDTO request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));

        // 1. 校验旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        // 2. 校验新密码一致性
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("新密码与确认密码不一致");
        }

        // 3. 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ===========================================
    // 2. 账号安全 (Account Security)
    // ===========================================

    @Override
    public List<LoginLogVO> getLoginLogs(Long userId, int page, int size) {
        
        // 1. 业务逻辑：校验用户ID (确保用户存在)
        if (!userRepository.existsById(userId)) {
            // 如果用户不存在，抛出 404 异常
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户ID [" + userId + "] 不存在。");
        }
        
        // 2. 调用 Repository 进行分页查询
        // Spring Data JPA 的页码是从 0 开始的，所以传入 (page - 1)
        // 假设 LoginLogRepository 中有 findByUserIdOrderByLoginTimeDesc 方法
        
        Pageable pageable = PageRequest.of(
            page - 1, 
            size
            // 如果需要排序，可以添加 .by("loginTime").descending() 
            // 如果方法名已包含 OrderBy，则 PageRequest 中可省略 Sort
        ); 
        
        // 假设 LoginLogRepository 提供了 findByUserId(Long userId, Pageable pageable)
        Page<LoginLogEntity> logEntities = this.loginLogRepository.findByUserIdOrderByLoginTimeDesc(userId, pageable);

        // 3. 转换 Entity 为 VO
        // 使用 Stream.toList() 替代 collect(Collectors.toList()) (Java 16+)
        return logEntities.getContent().stream()
                .map(this::convertToLoginLogVO) 
                .toList(); 
    }

    // ----------------------------------------------------
    // 【辅助方法】：Entity 到 VO 的转换
    // ----------------------------------------------------

    /**
     * 将 LoginLogEntity 转换为 LoginLogVO
     */
    private LoginLogVO convertToLoginLogVO(LoginLogEntity entity) {
        LoginLogVO vo = new LoginLogVO();
        vo.setLoginLogId(entity.getLoginLogId());
        vo.setLoginTime(entity.getLoginTime());
        vo.setIpAddress(entity.getIpAddress());
        vo.setDevice(entity.getDevice());
        vo.setStatus(entity.getStatus()); 
        vo.setIpAddress(entity.getIpAddress());

        // 【✅ 核心逻辑：实时 IP 解析】
        String ipAddress = entity.getIpAddress();
        
        if (ipAddress != null && !ipAddress.isEmpty()) {
            try {
                String resolvedLocation = geoLocationService.resolveIpToLocation(ipAddress);
                vo.setLoginLocation(resolvedLocation != null ? resolvedLocation : "未知地点");
            } catch (Exception e) {
                // 如果解析服务出现异常，记录错误并设置默认值
                logger.error("IP解析失败，IP: {}", ipAddress, e);
                vo.setLoginLocation("解析服务错误");
            }
        } else {
            vo.setLoginLocation("IP地址缺失");
        }

        return vo;
    }

    // =========================================================
    // 【实现 1】: 关注用户
    // =========================================================

    @Override
    @Transactional
    public void followUser(Long followerId, Long followingId) {
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
    public void unfollowUser(Long followerId, Long followingId) {
    
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


    /**
     * 辅助方法：将 FollowEntity 列表转换为 FollowUserVO 列表
     * @param followPage 包含 FollowEntity 的分页结果
     * @param currentUserId 当前登录用户ID (用于判断是否互相关注)
     * @param isFollowersList true表示正在查询粉丝列表 (FollowingId是目标用户)，false表示查询关注列表 (FollowerId是目标用户)
     */
    private FollowListVO buildFollowListResponse(Page<FollowEntity> followPage, Long currentUserId, boolean isFollowersList) {
        
        List<Long> targetUserIds = followPage.getContent().stream().map(entity -> isFollowersList ? entity.getFollowerId() : entity.getFollowingId()).toList();

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

    @Override
    public FollowListVO getFollowers(Long userId, int page, int limit, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 查找目标用户（userId）的粉丝列表 (即 FollowingId = userId)
        Page<FollowEntity> followPage = followRepository.findByFollowingId(userId, pageable);
        
        return buildFollowListResponse(followPage, currentUserId, true);
    }

    @Override
    public FollowListVO getFollowings(Long userId, int page, int limit, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 查找目标用户（userId）的关注列表 (即 FollowerId = userId)
        Page<FollowEntity> followPage = followRepository.findByFollowerId(userId, pageable);
        
        return buildFollowListResponse(followPage, currentUserId, false);
    }
}