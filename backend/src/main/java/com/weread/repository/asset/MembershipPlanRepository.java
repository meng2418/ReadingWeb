package com.weread.repository.asset;

import com.weread.entity.asset.MembershipPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlanEntity, Long> {
    
    /**
     * 获取所有在售的套餐
     */
    List<MembershipPlanEntity> findByIsAvailableTrue();
}