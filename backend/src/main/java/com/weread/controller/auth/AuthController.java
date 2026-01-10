package com.weread.controller.auth;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.weread.common.ApiResponse;
import com.weread.dto.request.auth.LoginRequest;
import com.weread.dto.request.auth.RegisterRequest;
import com.weread.dto.request.auth.ResetPasswordRequest;
import com.weread.dto.request.auth.SendCodeRequest;
import com.weread.dto.auth.RegisterDTO;
import com.weread.dto.auth.LoginDTO;
import com.weread.dto.auth.ResetPasswordDTO;
import com.weread.dto.auth.LoginResultVO;
import com.weread.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    // 添加这行
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-verification-code")
    public ApiResponse<Void> sendCode(@RequestBody SendCodeRequest request) {
        authService.sendSms(request.getPhone());
        return ApiResponse.ok();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LoginResultVO> register(@RequestBody RegisterRequest request) {
        RegisterDTO dto = new RegisterDTO();
        dto.setPhone(request.getPhone());
        dto.setUsername(request.getUsername());
        dto.setPassword(request.getPassword());
        dto.setConfirmPassword(request.getConfirmPassword());

        LoginResultVO result = authService.phoneRegister(dto);
        return ApiResponse.ok(result);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResultVO> login(@RequestBody LoginRequest request) {
        log.info("=== 收到登录请求 ===");
        log.info("请求参数: phone={}, password长度={}, type={}", 
            request.getPhone(), 
            request.getPassword() != null ? request.getPassword().length() : 0,
            request.getType());
        log.info("完整请求体: {}", request);
        
        try {
            LoginDTO dto = new LoginDTO();
            dto.setPhone(request.getPhone());
            dto.setPassword(request.getPassword());
            log.info("转换后的DTO: phone={}, password={}", dto.getPhone(), 
                dto.getPassword() != null ? "***长度:" + dto.getPassword().length() : "null");
            
            LoginResultVO result = authService.phonePasswordLogin(dto);
            log.info("登录成功! token={}, userId={}", 
                result.getToken() != null ? "有token" : "无token", 
                result.getUser().getUserId());
            
            return ApiResponse.ok(result);
            
        } catch (Exception e) {
            log.error("登录失败!", e);
            return ApiResponse.error(400, "登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/sms-login")
    public ApiResponse<LoginResultVO> smsLogin(@RequestBody LoginRequest request) {
        LoginDTO dto = new LoginDTO();
        dto.setPhone(request.getPhone());
        dto.setSms(request.getVerificationCode());

        LoginResultVO result = authService.smsLogin(dto);
        return ApiResponse.ok(result);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setPhoneNumber(request.getPhone());
        dto.setSmsCode(request.getVerificationCode());
        dto.setNewPassword(request.getNewPassword());
        dto.setConfirmPassword(request.getConfirmPassword());

        authService.resetPasswordBySms(dto);
        return ApiResponse.ok();
    }
}
