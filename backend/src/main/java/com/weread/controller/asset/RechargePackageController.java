package com.weread.controller.asset;

import com.weread.common.ApiResponse;
import com.weread.dto.asset.RechargeRequestDTO;
import com.weread.service.asset.RechargePackageService;
import com.weread.service.asset.RechargeService;
import com.weread.vo.asset.PaymentInfoVO;
import com.weread.vo.asset.RechargePackageVO;
import com.weread.vo.asset.RechargeResponseVO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/recharge")
@RequiredArgsConstructor
public class RechargePackageController {
    
    private final RechargePackageService rechargePackageService;
    private final RechargeService rechargeService;
    
    /**
     * 获取充值套餐列表
     * GET /recharge/packages
     */
    @GetMapping("/packages")
    public ApiResponse<List<RechargePackageVO>> getRechargePackages() {
        try {
            List<RechargePackageVO> data = rechargePackageService.getRechargePackages();
            return ApiResponse.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    // 2. 获取充值支付页信息
    @GetMapping("/payment-page")
    public ApiResponse<PaymentInfoVO> getPaymentPage(
            @RequestParam Integer packageId,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            if (userId == null) {
                return ApiResponse.error(401, "请先登录");
            }
            
            PaymentInfoVO data = rechargeService.getPaymentPageInfo(packageId, userId);
            return ApiResponse.ok(data);
            
        } catch (ResponseStatusException e) {
            return ApiResponse.error(e.getStatusCode().value(), e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "服务器内部错误");
        }
    }
    
    // 3. 创建充值订单
    @PostMapping
    public ApiResponse<RechargeResponseVO> recharge(
            @Valid @RequestBody RechargeRequestDTO request,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            if (userId == null) {
                return ApiResponse.error(401, "请先登录");
            }
            
            RechargeResponseVO data = rechargeService.createRechargeOrder(request, userId);
            return ApiResponse.ok(data);
            
        } catch (ResponseStatusException e) {
            return ApiResponse.error(e.getStatusCode().value(), e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "服务器内部错误");
        }
    }
}
