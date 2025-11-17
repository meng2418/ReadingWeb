package com.weread.repository.asset;

import com.weread.entity.asset.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // 根据订单ID和用户ID查找订单（防止越权）
    Optional<OrderEntity> findByOrderIdAndUserId(Long orderId, Long userId);
}