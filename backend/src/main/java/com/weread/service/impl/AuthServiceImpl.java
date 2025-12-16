package com.weread.service.impl;

import com.weread.config.SmsConfig;
import com.weread.dto.auth.LoginDTO;
import com.weread.dto.auth.RegisterDTO;
import com.weread.dto.auth.ResetPasswordDTO; // 新添加的DTO
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.UserRepository;
import com.weread.service.AuthService; // 必须实现接口
import com.weread.util.JwtUtil;
import java.time.LocalDateTime;

// 阿里云 SDK 导入
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;

// Spring/Java 核心依赖导入
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Random;
import java.util.concurrent.TimeUnit;

// 实现 AuthService 接口
@Service
public class AuthServiceImpl implements AuthService { 

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final SmsConfig smsConfig;
    
    // 短信客户端（实际连接阿里云）
    private final Client smsClient; 

    // 验证码在 Redis 中的 Key 前缀
    private static final String SMS_CODE_PREFIX = "SMS_CODE:";
    
    // =================================================================
    // 构造函数：注入所有依赖并初始化阿里云客户端
    // =================================================================
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           RedisTemplate<String, String> redisTemplate, JwtUtil jwtUtil, 
                           SmsConfig smsConfig) throws Exception {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
        this.smsConfig = smsConfig;
        
        // 阿里云短信客户端初始化
        Config config = new Config()
                .setAccessKeyId(smsConfig.getAccessKeyId())
                .setAccessKeySecret(smsConfig.getAccessKeySecret())
                // 设置 Endpoint，通常不需要更改
                .setEndpoint("dysmsapi.aliyuncs.com"); 
        
        // 确保 SDK 依赖正确
        this.smsClient = new Client(config);
    }

    // 辅助方法：获取 Redis Key
    private String getCodeRedisKey(String phone) {
        return SMS_CODE_PREFIX + phone;
    }

    // =================================================================
    // 通用短信发送逻辑 (AuthService 接口中必须定义)
    // 封装了阿里云的调用细节
    // =================================================================

    /**
     * 发送短信验证码的通用实现
     * @param phone 手机号
     */
    @Override
    public void sendSms(String phone) {
        // 频率限制：检查 60 秒内是否发送过
        if (redisTemplate.hasKey(getCodeRedisKey(phone))) {
            // 抛出 429 Too Many Requests
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "验证码发送过于频繁，请稍后再试。"); 
        }

        // 1. 生成验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        
        try {
            // 2. 构造阿里云短信请求
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(smsConfig.getSignName())
                .setTemplateCode(smsConfig.getTemplateCode())
                // 模板参数必须是 JSON 格式
                .setTemplateParam("{\"code\":\"" + code + "\"}"); 

            // 3. 调用阿里云 API
            SendSmsResponse response = smsClient.sendSms(sendSmsRequest);

            // 4. 检查 API 返回结果
            if (!"OK".equals(response.getBody().getCode())) {
                // 打印日志，方便排查阿里云返回的错误
                System.err.println("阿里云短信发送失败。Code: " + response.getBody().getCode() + ", Message: " + response.getBody().getMessage());
                // 抛出 500 Internal Server Error
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "短信服务商返回失败：" + response.getBody().getMessage());
            }

            // 5. 存储到 Redis：5 分钟有效期
            redisTemplate.opsForValue().set(getCodeRedisKey(phone), code, 5, TimeUnit.MINUTES);
            
        } catch (ResponseStatusException e) {
            // 重新抛出已封装的异常
            throw e;
        } catch (Exception e) {
            // 捕获所有其他异常（如网络错误、配置错误等）
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "发送短信失败，请检查配置或稍后再试。");
        }
    }


    // =================================================================
    // 【忘记密码相关方法实现】
    // =================================================================

    /**
     * 接口 1: 发送重置密码短信验证码
     * 业务逻辑：确认用户存在，然后调用通用发送短信方法。
     */
    @Override
    public void sendForgetPasswordSms(String phoneNumber) {
        // 1. 检查用户是否存在（必须已注册才能重置）
        if (!userRepository.existsByPhone(phoneNumber)) {
            // 抛出 404 Not Found 或 400 Bad Request
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该手机号未注册。");
        }
        
        // 2. 调用通用发送短信方法 (通用方法已包含频率限制和发送逻辑)
        this.sendSms(phoneNumber);
    }
    
    /**
     * 接口 2: 校验验证码并重置密码
     * @param dto 包含手机号、验证码、新密码和确认密码
     */
    @Override
    public void resetPasswordBySms(ResetPasswordDTO dto) {
        
        // 1. 新密码一致性校验
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "两次输入的新密码不一致。");
        }
        
        // 2. 查找用户实体
        UserEntity user = userRepository.findByPhone(dto.getPhoneNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在。"));
        
        // 3. 验证码校验
        String storedCode = redisTemplate.opsForValue().get(getCodeRedisKey(dto.getPhoneNumber()));
        
        if (storedCode == null || !storedCode.equals(dto.getSmsCode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "验证码错误或已过期。");
        }
        
        // 4. 新旧密码不能相同校验 (安全要求)
        // 检查新密码哈希后是否与数据库中旧密码的哈希值匹配
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "新密码不能与旧密码相同。");
        }
        
        // 5. 更新密码并清理
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user); // 持久化新密码
        
        // 6. 清理 Redis 中的验证码，防止二次使用
        redisTemplate.delete(getCodeRedisKey(dto.getPhoneNumber()));
    }
    
    
    // =================================================================
    // 【其他原有业务逻辑】
    // =================================================================
    
    @Override
    public UserEntity phoneRegister(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "两次输入的密码不一致。");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该手机号已被注册。");
        }

        UserEntity newUser = new UserEntity();
        newUser.setPhone(dto.getPhone());
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(newUser);
    }

    @Override
    public UserEntity phonePasswordLogin(LoginDTO dto) {
        UserEntity user = userRepository.findByPhone(dto.getPhone())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在或密码错误。"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在或密码错误。");
        }

        return user;
    }

    /**
     * 手机号 + 验证码登录 (包含完整的自动注册逻辑)
     */
    @Override
    public UserEntity smsLogin(LoginDTO dto) {
        
        // 1. 验证码校验
        String storedCode = redisTemplate.opsForValue().get(getCodeRedisKey(dto.getPhone()));

        if (storedCode == null || !storedCode.equals(dto.getSms())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "验证码错误或已过期。");
        }

        // 验证码校验成功，立即删除 Redis 中的验证码
        redisTemplate.delete(getCodeRedisKey(dto.getPhone()));

        // 2. 查找用户
        return userRepository.findByPhone(dto.getPhone())
            .orElseGet(() -> {
                // ==========================================================
                // 【自动注册逻辑：如果用户不存在，则创建新用户】
                // ==========================================================
                
                UserEntity newUser = new UserEntity();
                newUser.setPhone(dto.getPhone());
                
                // 自动生成一个随机密码占位符。
                // 理论上短信登录不需要密码，但数据库字段可能要求非空。
                // 使用一个长而随机的字符串并加密，确保安全性和不与用户设置的密码冲突。
                String randomPassword = String.valueOf(System.currentTimeMillis()) + new Random().nextInt(10000);
                newUser.setPassword(passwordEncoder.encode(randomPassword)); 
                
                // 自动生成一个默认用户名，例如 "用户" + 手机号后四位
                String defaultUsername;
                if (dto.getPhone().length() >= 4) {
                    defaultUsername = "用户" + dto.getPhone().substring(dto.getPhone().length() - 4);
                } else {
                    defaultUsername = "新用户";
                }
                newUser.setUsername(defaultUsername);
                
                newUser.setCreatedAt(LocalDateTime.now());
                
                // 保存新用户到数据库
                return userRepository.save(newUser);
            });
        
        // 3. 返回登录成功的用户实体
        // 在 orElseGet 内部已经返回了，所以这里不需要额外的 return
    }
}