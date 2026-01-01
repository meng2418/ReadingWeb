package com.weread.controller.asset;

import com.weread.common.ApiResponse;
import com.weread.service.asset.RechargePackageService;
import com.weread.vo.asset.RechargePackageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("recharge")
@RequiredArgsConstructor
public class RechargePackageController {
    
    private final RechargePackageService rechargePackageService;
    
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
}
