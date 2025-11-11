package com.weread.service;

import com.weread.config.SmsConfig;
import com.weread.dto.LoginDTO;
import com.weread.dto.RegisterDTO;
import com.weread.entity.UserEntity;
import com.weread.repository.UserRepository;
import com.weread.util.JwtUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final SmsConfig smsConfig;
    private final Client smsClient;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
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
                .setEndpoint("dysmsapi.aliyuncs.com"); 
        this.smsClient = new Client(config);
    }

    private String getCodeRedisKey(String phone) {
        return "SMS_CODE:" + phone;
    }

    /**
     * 发送短信验证码
     */
    public void sendVerificationCode(String phone) {
        if (redisTemplate.hasKey(getCodeRedisKey(phone))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "验证码发送过于频繁，请稍后再试。");
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        
        try {
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(smsConfig.getSignName())
                .setTemplateCode(smsConfig.getTemplateCode())
                .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = smsClient.sendSms(sendSmsRequest);

            if (!"OK".equals(response.getBody().getCode())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "短信服务商返回失败：" + response.getBody().getMessage());
            }

            redisTemplate.opsForValue().set(getCodeRedisKey(phone), code, 5, TimeUnit.MINUTES);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "发送短信失败，请检查配置或稍后再试。");
        }
    }

    /**
     * 手机号 + 密码注册
     */
    public UserEntity phoneRegister(RegisterDTO dto) {
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该手机号已被注册。");
        }

        UserEntity newUser = new UserEntity();
        newUser.setPhone(dto.getPhone());
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        // 其他默认值已经在 Entity 中设置

        return userRepository.save(newUser);
    }
    
    /**
     * 手机号 + 密码登录
     */
    public UserEntity phonePasswordLogin(LoginDTO dto) {
        UserEntity user = userRepository.findByPhone(dto.getPhone())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在或密码错误。"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在或密码错误。");
        }

        return user;
    }
    
    /**
     * 手机号 + 验证码登录 (包含自动注册逻辑)
     */
    public UserEntity codeLogin(LoginDTO dto) {
        String storedCode = redisTemplate.opsForValue().get(getCodeRedisKey(dto.getPhone()));

        if (storedCode == null || !storedCode.equals(dto.getVertificationcode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "验证码错误或已过期。");
        }

        redisTemplate.delete(getCodeRedisKey(dto.getPhone()));

        UserEntity user = userRepository.findByPhone(dto.getPhone())
            .orElseGet(() -> {
                // 如果用户不存在，自动注册
                UserEntity newUser = new UserEntity();
                newUser.setPhone(dto.getPhone());
                newUser.setPassword(passwordEncoder.encode(String.valueOf(System.currentTimeMillis()))); // 随机生成密码占位
                newUser.setUsername("用户" + dto.getPhone().substring(7));
                return userRepository.save(newUser);
            });
        
        return user;
    }
}