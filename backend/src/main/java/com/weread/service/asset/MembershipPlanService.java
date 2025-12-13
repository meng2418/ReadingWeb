package com.weread.service.asset;

import com.weread.entity.asset.MembershipPlanEntity;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanService {
    
    /**
     * 获取所有在售的套餐列表
     */
    List<MembershipPlanEntity> getAvailablePlans();

    /**
     * 根据ID获取单个套餐
     */
    Optional<MembershipPlanEntity> getPlanById(Integer planId);

    // 可以在这里提供一个初始化方法，将您的月卡、季卡、年卡数据插入数据库
    void initializeDefaultPlans();
}