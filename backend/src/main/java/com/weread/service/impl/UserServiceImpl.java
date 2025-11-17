package com.weread.service.impl;

import com.weread.dto.*; // 假设包含 SuccessMessage 等通用 DTO
import com.weread.service.EmailService;
import com.weread.service.UserService;
import com.weread.dto.user.UserDetailVO;
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.UserRepository;
import com.weread.dto.user.UpdateProfileDTO;
import com.weread.dto.user.UpdatePasswordDTO;
import com.weread.dto.user.LoginLogVO;
import com.weread.dto.auth.EmailCodeSendDTO;
import com.weread.dto.auth.BindEmailDTO;
import com.weread.dto.auth.UnbindEmailDTO;
import com.weread.util.RedisService; // 假设的Redis工具
import com.weread.util.SecurityUtils; // 假设的安全工具
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

// 假设的自定义异常
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
class InvalidCodeException extends RuntimeException {
    public InvalidCodeException(String message) { super(message); }
}


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final EmailService emailService;
    // 假设您还需要 Phone 相关的 DTO/VOs 和对应的逻辑，这里只展示 Email 部分。

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RedisService redisService,EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.emailService = emailService;
    }

    // ===========================================
    // 1. 个人信息 (Profile Management)
    // ===========================================

    @Override
    public UserDetailVO getUserProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));
        
        // 实际应用中，这里需要将 UserEntity 转换为 UserDetailVO
        UserDetailVO response = new UserDetailVO();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        // 注意：isMember 和 coins 等字段已不再由 UserService 负责
        
        return response;
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileDTO request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));

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
    public void updatePassword(Long userId, UpdatePasswordDTO request) {
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
    public void sendEmailBindCode(EmailCodeSendDTO request) {
    // 1. 业务逻辑校验 (检查邮箱是否已被占用)
    String receiverEmail = request.getReceiverEmail();
    Optional<UserEntity> existingUser = userRepository.findByEmail(receiverEmail);

    if (existingUser.isPresent()) {
        // 如果邮箱已经被其他用户占用，则抛出异常，阻止发送验证码
        // 确保提供一个明确的错误信息
        throw new IllegalArgumentException("该邮箱已被其他账号绑定，请更换邮箱");
    }

    // 2. 生成验证码
    String code = SecurityUtils.generateRandomCode(6);
    String redisKey = "BIND_EMAIL_CODE:" + receiverEmail; 
    
    // 3. 存储到 Redis (TTL: 5分钟)
    redisService.set(redisKey, code, 5, TimeUnit.MINUTES);
    
    // 4. 【核心】调用邮件服务发送
    emailService.sendVerificationCode(receiverEmail, code, "邮箱绑定验证码");
    }

    @Override
    public void bindEmail(Long userId, BindEmailDTO request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));
        
        String email = request.getEmail();
        String code = request.getVertificationCode();
        String redisKey = "BIND_EMAIL_CODE:" + email;
        
        // 1. 校验验证码
        String savedCode = redisService.get(redisKey);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new InvalidCodeException("验证码无效或已过期");
        }
        redisService.delete(redisKey);

        // 2. 检查邮箱是否已被占用 (二次校验)
        // ... (省略查询逻辑) ...
        
        // 3. 更新用户绑定信息
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void unbindEmail(Long userId, UnbindEmailDTO request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户未找到"));
        
        // 确保邮箱是已绑定的，并且解绑的验证码是发给这个已绑定的邮箱
        String currentEmail = user.getEmail();
        String code = request.getVertificationCode();
        
        if (currentEmail == null) {
            throw new IllegalArgumentException("用户未绑定邮箱");
        }
        
        // 假设解绑的验证码是通过另一个接口发到 currentEmail 的
        String redisKey = "UNBIND_EMAIL_CODE:" + currentEmail; 

        // 1. 校验验证码
        String savedCode = redisService.get(redisKey);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new InvalidCodeException("验证码无效或已过期");
        }
        redisService.delete(redisKey);

        // 2. 更新解绑信息
        user.setEmail(null);
        userRepository.save(user);
    }
    // ... 其他代码 ...

    @Override
    public List<LoginLogVO> getLoginLogs(Long userId, int page, int size) {
        // 实际业务逻辑：从 LoginLogRepository 查询数据并转换为 VO

        // 1. 业务逻辑：校验用户ID (确保用户存在)
        // userRepository.findById(userId)
    
        // 2. 调用 Repository 进行分页查询
        // Pageable pageable = PageRequest.of(page, size);
        // Page<LoginLogEntity> logEntities = loginLogRepository.findByUserId(userId, pageable);

        // 3. 转换 Entity 为 VO
        // return logEntities.getContent().stream()
        //         .map(this::convertToLoginLogVO) // 假设有一个转换方法
        //         .collect(Collectors.toList());


        // --- 存根实现（模拟数据）---
        if (page == 0) {
            LoginLogVO log = new LoginLogVO(
                1001L, 
                java.time.LocalDateTime.now().minusHours(2), 
                "112.98.12.1", 
                "iOS App 3.2",
                "SUCCESS"
            );
            return java.util.List.of(log);
        }
    return java.util.Collections.emptyList();
}

}