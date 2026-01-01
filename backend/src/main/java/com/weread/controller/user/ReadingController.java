package com.weread.controller.user;

import com.weread.service.user.ReadingService;
import com.weread.vo.user.TodayReadingTimeVO;
import com.weread.vo.user.ReadingTimeStatItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "阅读统计", description = "用户阅读时长统计相关接口")
public class ReadingController {
    
    private final ReadingService readingService;
    
    @Operation(summary = "获取今日阅读时长")
    @GetMapping("/reading-time/today")
    public ResponseEntity<Map<String, Object>> getTodayReadingTime(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId) {
        
        TodayReadingTimeVO result = readingService.getTodayReadingTime(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "获取用户本周/月/总阅读时长")
    @GetMapping("/reading-stats")
    public ResponseEntity<Map<String, Object>> getReadingStats(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId,
            @Parameter(description = "统计维度", required = true)
            @RequestParam String type,
            @Parameter(description = "查询年份")
            @RequestParam(required = false) Integer year) {
        
        List<ReadingTimeStatItemVO> stats = readingService.getReadingStats(userId, type, year);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }
}

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Tag(name = "奖励", description = "用户奖励相关接口")
class RewardController {
    
    private final ReadingService readingService;
    
    @Operation(summary = "领取阅读激励")
    @PostMapping("/reading")
    public ResponseEntity<Map<String, Object>> claimReadingReward(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId) {
        
        boolean success = readingService.claimReadingReward(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", success ? "领取成功" : "领取失败");
        response.put("data", null);
        
        return ResponseEntity.ok(response);
    }
}