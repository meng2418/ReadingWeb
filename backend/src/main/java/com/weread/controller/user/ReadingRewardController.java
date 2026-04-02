package com.weread.controller.user;

import com.weread.service.user.ReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阅读奖励相关接口（与 ReadingController 分离，避免与内嵌 Controller 重复映射）
 */
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Tag(name = "奖励", description = "用户奖励相关接口")
public class ReadingRewardController {

    private final ReadingService readingService;

    @Operation(summary = "获取今日已领取的奖励列表")
    @GetMapping("/reading/claimed")
    public ResponseEntity<Map<String, Object>> getTodayClaimedRewards(
            @Parameter(description = "用户ID", hidden = true)
            @AuthenticationPrincipal Integer userId) {

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "用户未登录或token无效");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            List<String> claimedTypes = readingService.getTodayClaimedRewardTypes(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", claimedTypes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage() != null ? e.getMessage() : "获取失败");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "领取阅读激励")
    @PostMapping("/reading")
    public ResponseEntity<Map<String, Object>> claimReadingReward(
            @Parameter(description = "用户ID", hidden = true)
            @AuthenticationPrincipal Integer userId,
            @Parameter(description = "阅读时长要求（分钟）", example = "30")
            @RequestParam(required = false, defaultValue = "30") Integer minutes) {

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "用户未登录或token无效");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            boolean success = readingService.claimReadingReward(userId, minutes);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", success ? "领取成功" : "领取失败");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", e.getStatusCode().value());
            response.put("message", e.getReason());
            response.put("data", null);
            return ResponseEntity.status(e.getStatusCode()).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage() != null ? e.getMessage() : "领取失败，请稍后重试");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
