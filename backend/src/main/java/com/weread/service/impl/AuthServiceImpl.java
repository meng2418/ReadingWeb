package com.weread.service.impl;

import com.weread.config.SmsConfig;
import com.weread.dto.auth.LoginDTO;
import com.weread.dto.auth.RegisterDTO;
import com.weread.dto.auth.ResetPasswordDTO;
import com.weread.entity.user.UserEntity;
import com.weread.repository.user.UserRepository;
import com.weread.service.AuthService;
import com.weread.util.JwtUtil;
import com.weread.util.TokenInfo;
import com.weread.vo.user.UserSimpleVO;
import com.weread.dto.auth.LoginResultVO;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final SmsConfig smsConfig;
    private final Client smsClient;

    private static final String SMS_CODE_PREFIX = "SMS_CODE:";

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            RedisTemplate<String, String> redisTemplate, JwtUtil jwtUtil,
            SmsConfig smsConfig) throws Exception {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
        this.smsConfig = smsConfig;

        Config config = new Config()
                .setAccessKeyId(smsConfig.getAccessKeyId())
                .setAccessKeySecret(smsConfig.getAccessKeySecret())
                .setEndpoint("dysmsapi.aliyuncs.com");
        this.smsClient = new Client(config);
    }

    private String getCodeRedisKey(String phone) {
        return SMS_CODE_PREFIX + phone;
    }

    // ---------------- 通用短信发送 ----------------
    @Override
    public void sendSms(String phone) {
        if (redisTemplate.hasKey(getCodeRedisKey(phone))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "验证码发送过于频繁");
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        try {
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsConfig.getSignName())
                    .setTemplateCode(smsConfig.getTemplateCode())
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = smsClient.sendSms(request);

            if (!"OK".equals(response.getBody().getCode())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "短信发送失败：" + response.getBody().getMessage());
            }

            redisTemplate.opsForValue().set(getCodeRedisKey(phone), code, 5, TimeUnit.MINUTES);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "短信发送失败");
        }
    }

    @Override
    public void sendForgetPasswordSms(String phoneNumber) {
        if (!userRepository.existsByPhone(phoneNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该手机号未注册");
        }
        sendSms(phoneNumber);
    }

    @Override
    public void resetPasswordBySms(ResetPasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "两次输入的新密码不一致");
        }

        UserEntity user = userRepository.findByPhone(dto.getPhoneNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        String storedCode = redisTemplate.opsForValue().get(getCodeRedisKey(dto.getPhoneNumber()));
        if (storedCode == null || !storedCode.equals(dto.getSmsCode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "验证码错误或已过期");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "新密码不能与旧密码相同");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        redisTemplate.delete(getCodeRedisKey(dto.getPhoneNumber()));
    }

    // ---------------- 注册 / 登录 ----------------
    @Override
    public LoginResultVO phoneRegister(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "两次输入的密码不一致");
        }

        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该手机号已被注册");
        }

        UserEntity user = new UserEntity();
        user.setPhone(dto.getPhone());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // 默认会员相关字段
        user.setIsMember(false);
        user.setMemberEndDate(LocalDateTime.now()); // 或者 LocalDateTime.now().minusDays(1) 表示非会员

        user.setCreatedAt(LocalDateTime.now());
        user.setCoins(0);
        user.setTotalReadingTime(0);
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        userRepository.save(user);

        TokenInfo tokenInfo = jwtUtil.generateTokenInfo(user.getPhone(), user.getUserId());
        return buildLoginResultVO(user, tokenInfo);
    }

    @Override
    public LoginResultVO phonePasswordLogin(LoginDTO dto) {
        UserEntity user = userRepository.findByPhone(dto.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在或密码错误"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在或密码错误");
        }

        TokenInfo tokenInfo = jwtUtil.generateTokenInfo(user.getPhone(), user.getUserId());
        return buildLoginResultVO(user, tokenInfo);
    }

    @Override
    public LoginResultVO smsLogin(LoginDTO dto) {
        String storedCode = redisTemplate.opsForValue().get(getCodeRedisKey(dto.getPhone()));
        if (storedCode == null || !storedCode.equals(dto.getSms())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "验证码错误或已过期");
        }
        redisTemplate.delete(getCodeRedisKey(dto.getPhone()));

        UserEntity user = userRepository.findByPhone(dto.getPhone())
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setPhone(dto.getPhone());
                    String randomPassword = System.currentTimeMillis() + String.valueOf(new Random().nextInt(10000));
                    newUser.setPassword(passwordEncoder.encode(randomPassword));
                    String defaultUsername = dto.getPhone().length() >= 4
                            ? "用户" + dto.getPhone().substring(dto.getPhone().length() - 4)
                            : "新用户";
                    newUser.setUsername(defaultUsername);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setIsMember(false);
                    newUser.setMemberEndDate(LocalDateTime.now());
                    newUser.setCoins(0);
                    newUser.setTotalReadingTime(0);
                    newUser.setFollowerCount(0);
                    newUser.setFollowingCount(0);
                    return userRepository.save(newUser);
                });

        TokenInfo tokenInfo = jwtUtil.generateTokenInfo(user.getPhone(), user.getUserId());
        return buildLoginResultVO(user, tokenInfo);
    }

    // ---------------- 辅助方法 ----------------
    private LoginResultVO buildLoginResultVO(UserEntity user, TokenInfo tokenInfo) {
    LoginResultVO vo = new LoginResultVO();
    UserSimpleVO userVO = new UserSimpleVO();
    userVO.setUserId(user.getUserId());
    userVO.setUsername(user.getUsername());
    userVO.setAvatar(user.getAvatar());
    userVO.setBio(user.getBio());

    vo.setUser(userVO);
    vo.setToken(tokenInfo.getAccessToken());
    return vo;
}

}
