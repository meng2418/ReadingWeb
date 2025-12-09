package com.weread.controller.asset;

import com.weread.entity.asset.MembershipPlanEntity;
import com.weread.service.asset.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membershipPlan")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    /**
     * GET /api/v1/product/membership/plans
     * 获取所有当前在售的会员套餐列表（例如：月卡、季卡、年卡及其价格）
     */
    @GetMapping("/plans")
    public List<MembershipPlanEntity> getAvailableMembershipPlans() {
        return membershipPlanService.getAvailablePlans();
    }

    /**
     * GET /api/v1/product/membership/plans/{planId}
     * 根据ID获取单个套餐的详细信息
     */
    @GetMapping("/plans/{planId}")
    public ResponseEntity<MembershipPlanEntity> getPlanDetails(@PathVariable Integer planId) {
        return membershipPlanService.getPlanById(planId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}