package com.weread.controller.community;

import com.weread.dto.community.LikeRequestDTO;
import com.weread.service.community.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Tag(name = "点赞", description = "点赞相关接口")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "点赞")
    @PostMapping
    public ResponseEntity<Map<String, Object>> like(
            @Valid @RequestBody LikeRequestDTO request,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            Map<String, Object> result = likeService.toggleLike(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "点赞失败: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}