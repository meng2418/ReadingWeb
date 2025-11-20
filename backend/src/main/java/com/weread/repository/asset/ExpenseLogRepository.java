package com.weread.repository.asset;

import com.weread.entity.asset.ExpenseLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ExpenseLogRepository extends JpaRepository<ExpenseLogEntity, Long> {
    // 分页查询用户的消费记录
    Page<ExpenseLogEntity> findByUserId(Long userId, Pageable pageable);
}