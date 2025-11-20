package com.weread.repository.asset;

import com.weread.entity.asset.RechargeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface RechargeLogRepository extends JpaRepository<RechargeLogEntity, Long> {
    // 分页查询用户的充值记录
    Page<RechargeLogEntity> findByUserId(Long userId, Pageable pageable);
}