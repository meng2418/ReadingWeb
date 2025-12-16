package com.weread.controller.asset;

import com.weread.service.asset.TrialCardService;
import com.weread.vo.asset.TrialBalanceVO; // 需要一个 TrialBalanceVO
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trial")
@RequiredArgsConstructor
public class TrialCardController {

    private final TrialCardService trialCardService;

    /**
     * GET /api/v1/trial/balance
     * 查询用户当前剩余的体验卡天数余额
     */
    @GetMapping("/balance")
    public TrialBalanceVO getTrialBalance(@RequestAttribute("userId") Long userId) {
        int balance = trialCardService.getTrialDaysBalance(userId);
        
        // 假设 TrialBalanceVO 只需要一个字段来返回余额
        return TrialBalanceVO.builder()
                .userId(userId)
                .daysBalance(balance)
                .build();
    }

    /**
     * POST /api/v1/trial/reward
     * 领取体验卡奖励（例如：每日签到奖励）
     */
    @PostMapping("/reward")
    public ResponseEntity<?> claimTrialReward(@RequestAttribute("userId") Long userId, 
                                              @RequestParam int days, 
                                              @RequestParam String type) {
        
        // 实际业务中，这里会先进行复杂的校验（例如：是否已签到）
        trialCardService.addTrialDays(userId, days, type);
        return ResponseEntity.ok().body("成功领取 " + days + " 天体验卡");
    }
}