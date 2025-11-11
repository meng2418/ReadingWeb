package com.weread.controller;

import com.weread.dto.LoginDTO;
import com.weread.dto.RegisterDTO;
import com.weread.dto.SmsCodeDTO;
import com.weread.dto.LoginResponse;
import com.weread.entity.UserEntity;
import com.weread.service.AuthService;
import com.weread.util.JwtUtil;
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

    private LoginResponse createLoginResponse(UserEntity user) {
        String token = jwtUtil.generateToken(user.getPhone(), user.getId());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setPhone(user.getPhone());
        response.setUsername(user.getUsername());
        return response;
    }

    /**
     * 1. 手机号+密码注册接口
     * POST /api/auth/register
     */
    @PostMapping("/passwordRegister")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterDTO dto) {
        UserEntity newUser = authService.phoneRegister(dto); 
        // 注册成功后直接登录并返回 Token
        return ResponseEntity.status(HttpStatus.CREATED).body(createLoginResponse(newUser));
    }
    
    /**
     * 2. 手机号+密码登录接口
     * POST /api/auth/login/password
     */
    @PostMapping("/password/login")
    public ResponseEntity<LoginResponse> passwordLogin(@RequestBody LoginDTO dto) {
        UserEntity user = authService.phonePasswordLogin(dto);
        return ResponseEntity.ok(createLoginResponse(user));
    }

    /**
     * 3. 发送短信验证码接口
     * POST /api/auth/code/send
     */
    @PostMapping("/sendvertificationcode")
    public ResponseEntity<String> sendCode(@RequestBody SmsCodeDTO dto) {
        authService.sendVerificationCode(dto.getPhone());
        return ResponseEntity.ok("验证码发送成功，请注意查收。");
    }

    /**
     * 4. 手机号+验证码登录接口 (包含自动注册逻辑)
     * POST /api/auth/login/code
     */
    @PostMapping("/vertificationcodeLogin")
    public ResponseEntity<LoginResponse> codeLogin(@RequestBody LoginDTO dto) {
        UserEntity user = authService.codeLogin(dto);
        return ResponseEntity.ok(createLoginResponse(user));
    }
}
