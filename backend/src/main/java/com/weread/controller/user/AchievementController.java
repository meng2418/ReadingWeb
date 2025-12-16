package com.weread.controller.user;

import com.weread.entity.user.AchievementLogEntity;
import com.weread.service.user.AchievementService;
import com.weread.service.user.UserStatService;
import com.weread.vo.user.UserStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/stat")
@RequiredArgsConstructor
public class AchievementController {

    private final UserStatService userStatService;
    private final AchievementService achievementService;

    /**
     * GET /api/v1/user/stat/summary
     * 获取用户所有累计统计数据 (书籍、笔记、时长)
     */
    @GetMapping("/summary")
    public UserStatVO getUserStatSummary(@RequestAttribute("userId") Long userId) {
        return userStatService.getUserStats(userId);
    }

    /**
     * GET /api/v1/user/stat/achievement
     * 获取用户最新的最高级阅历成就记录 (用于“阅历”版块展示)
     */
    @GetMapping("/achievement")
    public List<AchievementLogEntity> getLatestAchievement(@RequestAttribute("userId") Long userId) {
        // Service 已经实现了只返回最新最高成就的逻辑
        return achievementService.getAchievementsByUserId(userId);
    }
    
    // ------------------------------------------------------------------
    // 内部/事件触发接口 (通常由其他服务调用，例如阅读器)
    // ------------------------------------------------------------------

    // 假设阅读器每次完成一本书时调用
    @PostMapping("/event/book-completed")
    public ResponseEntity<?> logBookCompleted(@RequestAttribute("userId") Long userId) {
        // 假设只增加 1 本书
        userStatService.incrementBookCount(userId, 1);
        return ResponseEntity.ok().build();
    }
    
    // 假设笔记服务每次创建笔记时调用
    @PostMapping("/event/note-created")
    public ResponseEntity<?> logNoteCreated(@RequestAttribute("userId") Long userId) {
        // 假设只增加 1 条笔记
        userStatService.incrementNoteCount(userId, 1);
        return ResponseEntity.ok().build();
    }
}