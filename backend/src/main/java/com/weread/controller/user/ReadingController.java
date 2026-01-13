package com.weread.controller.user;

import com.weread.service.user.ReadingService;
import com.weread.vo.user.TodayReadingTimeVO;
import com.weread.vo.user.TopBooksVO;
import com.weread.vo.user.MilestoneVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
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
            @AuthenticationPrincipal Integer userId) {
        
        TodayReadingTimeVO result = readingService.getTodayReadingTime(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reading-stats/timeline")
    public ResponseEntity<Map<String, Object>> getReadingTimeline(
            @AuthenticationPrincipal Integer userId) {

        Map<String, Object> stats = readingService.getReadingStatsTimeline(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取用户特定时间段阅读最久的3本书")
    @GetMapping("/reading-stats/top-books")
    public ResponseEntity<Map<String, Object>> getTopBooksByPeriod(
            @Parameter(description = "用户ID", hidden = true)
            @AuthenticationPrincipal Integer userId,
            @Parameter(description = "时间段：week-本周，month-本月，year-今年，total-总计", 
                       example = "week")
            @RequestParam(required = false, defaultValue = "week") String period) {
        
        TopBooksVO result = readingService.getTopBooksByPeriod(userId, period);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "获取用户阅历最新里程碑")
    @GetMapping("/latest-milestones")
    public ResponseEntity<Map<String, Object>> getLatestMilestones(
            @Parameter(description = "用户ID", hidden = true)
            @AuthenticationPrincipal Integer userId) {
        
        MilestoneVO result = readingService.getLatestMilestones(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
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
            @AuthenticationPrincipal Integer userId,
            @Parameter(description = "阅读时长要求（分钟）", example = "30")
            @RequestParam(required = false, defaultValue = "30") Integer minutes) {
        
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
        }
    }
}