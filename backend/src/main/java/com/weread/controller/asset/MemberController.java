package com.weread.controller.asset;

import com.weread.entity.asset.MemberEntity;
import com.weread.service.asset.MemberService;
import com.weread.service.asset.TrialCardService;
import com.weread.vo.asset.MemberStatusVO; // 需要定义这个 VO
import com.weread.repository.asset.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TrialCardService trialCardService;

    /**
     * GET /api/v1/member/status
     * 获取用户的综合会员状态（正式会员过期时间、体验卡余额）
     */
    @GetMapping("/status")
public MemberStatusVO getMemberStatus(@RequestAttribute("userId") Long userId) {
    
    // 1. 通过 Service 获取 Entity
    Optional<MemberEntity> memberOpt = memberService.getMemberEntityByUserId(userId); 
    
    // 2. 获取其他所需数据（例如体验卡余额）
    int trialDays = trialCardService.getTrialDaysBalance(userId);
    
    // 3. 将 Entity 实例和数据传递给 VO 的静态方法进行构建
    return MemberStatusVO.buildFromEntities(
        memberOpt.orElse(null), 
        trialDays
    );
}
    
    /**
     * POST /api/v1/member/activate
     * 这是一个内部或管理员接口，用于在支付成功回调后触发会员开通/续费
     * 实际在生产环境中，这个逻辑通常在支付成功回调的服务中被调用
     */
    @PostMapping("/activate")
    public ResponseEntity<?> activateMember(@RequestAttribute("userId") Long userId, 
                                            @RequestParam int days) {
        memberService.activateMember(userId, days);
        return ResponseEntity.ok("会员续费/激活成功。");
    }
}