package com.weread.service.impl.asset;

import com.weread.entity.asset.MembershipPlanEntity;
import com.weread.repository.asset.MembershipPlanRepository;
import com.weread.service.asset.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct; 
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    // --- 默认套餐数据 ---
    // 定义您的月卡、季卡、年卡数据，单位：天 和 分
    private static final List<MembershipPlanEntity> DEFAULT_PLANS = List.of(
        createPlan("月卡", 30, 3000, "连续包月，每月30元，享受30天会员权益"), // 30元 = 3000分
        createPlan("季卡", 90, 6000, "购买3个月会员，限时优惠价60元"),      // 60元 = 6000分
        createPlan("年卡", 365, 22800, "购买12个月会员，年度最优价228元")  // 228元 = 22800分
    );

    private static MembershipPlanEntity createPlan(String name, int days, int cents, String description) {
        MembershipPlanEntity plan = new MembershipPlanEntity();
        plan.setName(name);
        plan.setDurationDays(days);
        plan.setPriceCents(cents);
        plan.setDescription(description);
        plan.setIsAvailable(true);
        return plan;
    }

    /**
     * 【初始化方法】
     * 在服务启动后执行，检查数据库中是否存在套餐数据，如果不存在则初始化默认套餐。
     */
    @Override
    @PostConstruct
    @Transactional
    public void initializeDefaultPlans() {
        if (planRepository.count() == 0) {
            planRepository.saveAll(DEFAULT_PLANS);
            System.out.println("✅ MembershipPlanService: 默认会员套餐数据初始化完成。");
        } else {
            System.out.println("ℹ️ MembershipPlanService: 数据库中已存在套餐数据，跳过初始化。");
        }
    }


    @Override
    public List<MembershipPlanEntity> getAvailablePlans() {
        // 使用 Repository 提供的查询方法，只查找在售的套餐
        return planRepository.findByIsAvailableTrue();
    }

    @Override
    public Optional<MembershipPlanEntity> getPlanById(Integer planId) {
        return planRepository.findById(planId);
    }
}