package com.weread.repository.asset;

import com.weread.entity.asset.RechargeOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RechargeOrderRepository extends JpaRepository<RechargeOrderEntity, Long> {
    
    // 根据订单号查询
    Optional<RechargeOrderEntity> findByOrderNo(String orderNo);
    
    // 查询用户订单
    Optional<RechargeOrderEntity> findByOrderNoAndUserId(String orderNo, Integer userId);
    
    // 更新订单状态
    @Modifying
    @Query("UPDATE RechargeOrderEntity o SET o.orderStatus = :status, o.updatedAt = CURRENT_TIMESTAMP WHERE o.orderNo = :orderNo")
    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") RechargeOrderEntity.OrderStatus status);
    
    // 支付成功回调
    @Modifying
    @Query("UPDATE RechargeOrderEntity o SET " +
           "o.orderStatus = :paidStatus, " +
           "o.transactionNo = :transactionNo, " +
           "o.paidAt = :paidAt, " +
           "o.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE o.orderNo = :orderNo AND o.orderStatus = :pendingStatus")
    int markAsPaid(@Param("orderNo") String orderNo, 
                   @Param("transactionNo") String transactionNo,
                   @Param("paidAt") LocalDateTime paidAt,
                   @Param("paidStatus") RechargeOrderEntity.OrderStatus paidStatus,
                   @Param("pendingStatus") RechargeOrderEntity.OrderStatus pendingStatus);
    
    // 检查是否有未完成的订单
    boolean existsByUserIdAndPackageIdAndOrderStatus(Integer userId, Integer packageId, RechargeOrderEntity.OrderStatus status);
}