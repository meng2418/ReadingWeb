package com.weread.controller.asset;

import com.weread.dto.asset.PurchaseMembershipDTO;
import com.weread.service.asset.MembershipService;
import com.weread.vo.asset.MembershipPackageVO;
import com.weread.vo.asset.MembershipPaymentInfoVO;
import com.weread.vo.asset.PurchaseResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("membership")
@RequiredArgsConstructor
@Tag(name = "会员服务", description = "会员套餐购买与开通接口")
@Validated
public class MembershipController {
    
    private final MembershipService membershipService;
    
    @Operation(summary = "获取会员套餐列表")
    @GetMapping("/packages")
    public ResponseEntity<Map<String, Object>> getMembershipPackages()
         {
        
        List<MembershipPackageVO> packages = membershipService.getMembershipPackages();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", packages);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "获取会员充值页")
    @GetMapping("/payment-info")
    public ResponseEntity<Map<String, Object>> getPaymentInfo(
            @Parameter(description = "套餐ID", required = true)
            @RequestParam Integer packageId,
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId) {
        
        MembershipPaymentInfoVO paymentInfo = membershipService.getPaymentInfo(packageId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", paymentInfo);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "开通会员")
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseMembership(
            @Valid @RequestBody PurchaseMembershipDTO purchaseDTO,
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId) {
        
        PurchaseResultVO result = membershipService.purchaseMembership(purchaseDTO, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "开通成功");
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }
}
