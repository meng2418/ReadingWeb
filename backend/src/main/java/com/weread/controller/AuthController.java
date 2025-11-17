package com.weread.controller;

import com.weread.dto.auth.AuthTokenVO;
import com.weread.dto.auth.LoginDTO;
import com.weread.dto.auth.UserBasicVO;
import com.weread.entity.user.UserEntity;
import com.weread.dto.auth.LoginVO;
import com.weread.dto.auth.RegisterDTO;
import com.weread.dto.auth.PhoneCodeSendDTO;
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
    private LoginVO createLoginResponse(UserEntity user) {
    // 1
    // 1. 调用 JwtUtil 生成完整的 Token 信息
        // 假设 JwtUtil.generateTokenInfo 方法返回 TokenInfo 对象
        TokenInfo tokenInfo = jwtUtil.generateTokenInfo(user.getPhone(), user.getId()); 

        // 2. 封装 AuthTokenVO (凭证信息)
        AuthTokenVO authTokenVO = new AuthTokenVO();
        authTokenVO.setAccessToken(tokenInfo.getAccessToken());
        authTokenVO.setRefreshToken(tokenInfo.getRefreshToken());
        authTokenVO.setExpiresIn(tokenInfo.getExpiresIn());
        
        // 3. 封装 UserBasicVO (用户基本信息)
        UserBasicVO userBasicVO = new UserBasicVO();
        userBasicVO.setUserId(user.getId());
        userBasicVO.setPhone(user.getPhone());
        userBasicVO.setUsername(user.getUsername());
        userBasicVO.setAvatar(user.getAvatar()); // 假设 UserEntity 有 getAvatar() 方法

        // 4. 封装 LoginResponseVO (最终响应体)
        LoginVO response = new LoginVO();
        response.setToken(authTokenVO);
        response.setUser(userBasicVO);
        
        return response;
    }

    /**
     * 1. 手机号+密码注册接口
     * POST /api/auth/register
     */
    @PostMapping("/passwordRegister")
    public ResponseEntity<LoginVO> register(@Valid@RequestBody RegisterDTO dto) {
        UserEntity newUser = authService.phoneRegister(dto); 
        // 注册成功后直接登录并返回 Token
        return ResponseEntity.status(HttpStatus.CREATED).body(createLoginResponse(newUser));
    }
    
    /**
     * 2. 手机号+密码登录接口
     * POST /api/auth/login/password
     */
    @PostMapping("/password/login")
    public ResponseEntity<LoginVO> passwordLogin(@Valid @RequestBody LoginDTO dto) {
        UserEntity user = authService.phonePasswordLogin(dto);
        return ResponseEntity.ok(createLoginResponse(user));
    }

    /**
     * 3. 发送短信验证码接口
     * POST /api/auth/code/send
     */
    @PostMapping("/sendvertificationcode")
    public ResponseEntity<String> sendCode(@Valid@RequestBody PhoneCodeSendDTO dto) {
        authService.sendVerificationCode(dto.getPhone());
        return ResponseEntity.ok("验证码发送成功，请注意查收。");
    }

    /**
     * 4. 手机号+验证码登录接口 (包含自动注册逻辑)
     * POST /api/auth/login/code
     */
    @PostMapping("/vertificationcodeLogin")
    public ResponseEntity<LoginVO> codeLogin(@Valid@RequestBody LoginDTO dto) {
        UserEntity user = authService.codeLogin(dto);
        return ResponseEntity.ok(createLoginResponse(user));
    }
}
