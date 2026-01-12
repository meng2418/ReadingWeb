package com.weread.controller.home;

import com.weread.common.ApiResponse;
import com.weread.dto.book.SimpleBookDTO;
import com.weread.dto.reading.ReadingStatsDTO;
import com.weread.dto.reading.RecentBookDTO;
import com.weread.entity.user.UserEntity;
import com.weread.service.user.ReadingStatsService;
import com.weread.service.user.RecentBookService;
import com.weread.service.book.RecommendationService;
import com.weread.service.list.RankingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private ReadingStatsService readingStatsService;

    @Autowired
    private RecentBookService recentBookService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RankingService rankingService;

    @GetMapping("/reading-stats")
    public ApiResponse<ReadingStatsDTO> getReadingStats() {
        try {
            Integer userId = getCurrentUserId();
            ReadingStatsDTO stats = readingStatsService.getReadingStats(userId);
            return ApiResponse.ok(stats);
        } catch (RuntimeException e) {
            // 用户未认证或认证失败
            return ApiResponse.error(401, "用户未登录: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常
            e.printStackTrace();
            return ApiResponse.error(500, "获取阅读统计失败");
        }
    }

    @GetMapping("/recent-books")
    public ApiResponse<List<RecentBookDTO>> getRecentBooks() {
        try {
            Integer userId = getCurrentUserId();
            List<RecentBookDTO> recentBooks = recentBookService.getRecentBooks(userId, 4);
            return ApiResponse.ok(recentBooks);
        } catch (RuntimeException e) {
            // 用户未认证或认证失败
            return ApiResponse.error(401, "用户未登录: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常
            e.printStackTrace();
            return ApiResponse.error(500, "获取最近阅读书籍失败");
        }
    }

    @GetMapping("/recommendations")
    @Operation(summary = "获取猜你想看书籍（6本）", description = "随机返回6本推荐书籍", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<SimpleBookDTO>> getRecommendations() {
        try {
            List<SimpleBookDTO> recommendations = recommendationService.getRecommendations();

            // 确保返回6本（如果不足，前端可以处理）
            return ApiResponse.ok(recommendations);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取推荐书籍失败");
        }
    }

    @GetMapping("/rankings/{type}")
    @Operation(summary = "获取书籍榜单（10本）", description = """
            - weekly: 周榜（近期热度+质量）
            - monthly: 月榜（长期热度+质量）
            - new: 新书榜（最近3个月，按评分）
            - masterpiece: 神作榜（高质量经典）
            """, security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<SimpleBookDTO>> getRankings(@PathVariable String type) {
        try {
            List<SimpleBookDTO> rankingBooks = rankingService.getRankingBooks(type);
            return ApiResponse.ok(rankingBooks);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取榜单失败: " + e.getMessage());
        }
    }

    /**
     * 从SecurityContext获取当前登录用户ID
     * 与书架控制器保持一致
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }

        Object principal = authentication.getPrincipal();
    
        // 直接从 Integer 类型的 Principal 获取用户ID
        if (principal instanceof Integer) {
            return (Integer) principal;
        }
    
        // 如果 Principal 是 UserEntity，则从中获取用户ID
        if (principal instanceof UserEntity) {
            UserEntity user = (UserEntity) principal;
            Object userId = user.getUserId();
        
            if (userId instanceof Integer) {
                return (Integer) userId;
            } else if (userId instanceof Long) {
                return ((Long) userId).intValue();
            }
        }
    
        throw new RuntimeException("未知的Principal类型：" + principal.getClass().getName());
    }
}