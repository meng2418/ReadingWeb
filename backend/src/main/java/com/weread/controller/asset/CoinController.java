package com.weread.controller.asset;

import com.weread.dto.asset.CoinDepositDTO;
import com.weread.service.asset.CoinService;
import com.weread.vo.asset.TransactionReceiptVO;
import com.weread.vo.asset.CoinAccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/coin")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    /**
     * GET /api/v1/coin/balance
     * 获取用户当前充值币余额
     */
    @GetMapping("/balance")
    public CoinAccountVO getBalance(@RequestAttribute("userId") Long userId) {
        // 假设 CoinService 中有一个方法可以将 Entity 转换为 CoinAccountVO
        return CoinAccountVO.fromEntity(coinService.getOrCreateAccount(userId));
    }

    /**
     * POST /api/v1/coin/deposit
     * 模拟用户发起充值请求，并完成入账
     */
    @PostMapping("/deposit")
    public TransactionReceiptVO deposit(@RequestAttribute("userId") Long userId, 
                                        @Valid @RequestBody CoinDepositDTO dto) {
        // 注意：这里调用的是模拟支付流程
        return coinService.processSimulatedDeposit(userId, dto.getMoneyAmount());
    }
    
    /**
     * POST /api/v1/coin/purchase
     * 用户使用充值币购买书籍
     * * @param bookId 购买的书籍ID
     * @param price  书籍价格（充值币）
     */
    @PostMapping("/purchase/{bookId}")
    public ResponseEntity<?> purchaseBook(@RequestAttribute("userId") Long userId, 
                                          @PathVariable Long bookId, 
                                          @RequestParam Integer price) {
        
        // 业务逻辑放在 Service 层，Controller 只负责调用和处理异常
        try {
            coinService.purchaseBook(userId, bookId, price);
            return ResponseEntity.ok().build(); // 返回 200 OK，表示购买成功
        } catch (RuntimeException e) {
            // 返回 400 Bad Request 或其他自定义错误，如余额不足
            return ResponseEntity.badRequest().body(e.getMessage()); 
        }
    }
}