package com.weread.controller;


import com.weread.dto.auth.LoginDTO; 
import com.weread.dto.auth.RegisterDTO; 
import com.weread.dto.auth.SendSmsDTO; 
import com.weread.entity.user.UserEntity;
import com.weread.vo.auth.LoginVO;
import com.weread.vo.auth.AuthTokenVO;
import com.weread.vo.user.UserDetailVO;

import com.weread.service.AuthService;
import com.weread.util.JwtUtil;
import com.weread.util.TokenInfo;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }
    
    private LoginVO createLoginVO(UserEntity user) {
    // 1. 调用 JwtUtil 生成完整的 Token 信息
        // 假设 JwtUtil.generateTokenInfo 方法返回 TokenInfo 对象
        TokenInfo tokenInfo = jwtUtil.generateTokenInfo(user.getPhone(), user.getUserId()); 

        // 2. 封装 AuthTokenVO (凭证信息)
        AuthTokenVO authTokenVO = new AuthTokenVO();
        authTokenVO.setAccessToken(tokenInfo.getAccessToken());
        authTokenVO.setRefreshToken(tokenInfo.getRefreshToken());
        authTokenVO.setExpiresIn(tokenInfo.getExpiresIn());
        
        // 3. 封装 UserDetailVO (用户基本信息)
        UserDetailVO userDetailVO = new UserDetailVO(); 
        userDetailVO.setUserId(user.getUserId());
        userDetailVO.setPhone(user.getPhone());
        userDetailVO.setUsername(user.getUsername());
        userDetailVO.setAvatar(user.getAvatar()); 

        // 4. 封装 LoginVO (最终响应体)
        LoginVO vo = new LoginVO();
        vo.setToken(authTokenVO);
        vo.setUser(userDetailVO); 
        
        return vo;
    }

    /**
     * 1. 手机号+密码注册接口
     * POST /api/auth/register
     */
    @PostMapping("/passwordRegister")
    public ResponseEntity<LoginVO> passwordRegister(@Valid@RequestBody RegisterDTO dto) { 
        UserEntity newUser = authService.phoneRegister(dto); 
        // 注册成功后直接登录并返回 Token
        return ResponseEntity.status(HttpStatus.CREATED).body(createLoginVO(newUser));
    }
    
    /**
     * 2. 手机号+密码登录接口
     * POST /api/auth/login/password
     */
    @PostMapping("/passwordLogin")
    public ResponseEntity<LoginVO> passwordLogin(@Valid @RequestBody LoginDTO dto) {
        UserEntity user = authService.phonePasswordLogin(dto);
        return ResponseEntity.ok(createLoginVO(user));
    }

    /**
     * 3. 发送短信验证码接口
     * POST /api/auth/snedSms
     */
    @PostMapping("/sendSms")
    public ResponseEntity<String> sendSms(@Valid@RequestBody SendSmsDTO dto) {
        authService.sendSms(dto.getPhoneNumber());
        return ResponseEntity.ok("验证码发送成功，请注意查收。");
    }

    /**
     * 4. 手机号+验证码登录接口 (包含自动注册逻辑)
     * POST /api/auth/smsLogin
     */
    @PostMapping("/smsLogin")
    public ResponseEntity<LoginVO> smsLogin(@Valid@RequestBody LoginDTO dto) {
        UserEntity user = authService.smsLogin(dto);
        return ResponseEntity.ok(createLoginVO(user));
    }
}