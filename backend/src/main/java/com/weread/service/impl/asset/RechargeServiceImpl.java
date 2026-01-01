package com.weread.service.impl.asset;

import com.weread.dto.asset.RechargeRequestDTO;
import com.weread.entity.asset.RechargeOrderEntity;
import com.weread.entity.asset.RechargePackageEntity;
import com.weread.repository.asset.RechargeOrderRepository;
import com.weread.repository.asset.RechargePackageRepository;
import com.weread.repository.user.UserRepository;
import com.weread.service.asset.RechargeService;
import com.weread.vo.asset.PaymentInfoVO;
import com.weread.vo.asset.RechargeResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RechargeServiceImpl implements RechargeService {
    
    private final RechargePackageRepository rechargePackageRepository;
    private final RechargeOrderRepository rechargeOrderRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public PaymentInfoVO getPaymentPageInfo(Integer packageId, Integer userId) {
        // 验证套餐是否存在且启用 - 这里需要用Optional包裹
        RechargePackageEntity packageEntity = rechargePackageRepository
            .findByPackageIdAndIsActiveTrue(packageId)  // 这个方法返回Optional
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "套餐不存在或已下架"));
        
        // 构建支付页信息
        PaymentInfoVO vo = new PaymentInfoVO();
        vo.setPackageId(packageEntity.getPackageId());
        vo.setCoinAmount(packageEntity.getCoinAmount());
        vo.setCnyAmount(BigDecimal.valueOf(packageEntity.getCnyAmount()));
        vo.setBonusCoins(packageEntity.getBonusCoins());
        vo.setPayAmount(BigDecimal.valueOf(packageEntity.getCnyAmount()));
        vo.setTotalCoins(packageEntity.getCoinAmount() + packageEntity.getBonusCoins());
        
        return vo;
    }
    
    @Override
    @Transactional
    public RechargeResponseVO createRechargeOrder(RechargeRequestDTO request, Integer userId) {
        // 1. 验证套餐 - 这里同样需要用Optional包裹
        RechargePackageEntity packageEntity = rechargePackageRepository
            .findByPackageIdAndIsActiveTrue(request.getPackageId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "套餐不存在或已下架"));
        
        // 2. 检查是否有未完成的相同套餐订单
        if (rechargeOrderRepository.existsByUserIdAndPackageIdAndOrderStatus(
                userId, request.getPackageId(), RechargeOrderEntity.OrderStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "您有未完成的相同套餐订单");
        }
        
        // 3. 创建订单
        RechargeOrderEntity order = new RechargeOrderEntity();
        order.setUserId(userId);
        order.setPackageId(request.getPackageId());
        order.setCoinAmount(packageEntity.getCoinAmount());
        order.setBonusCoins(packageEntity.getBonusCoins());
        order.setPayAmount(BigDecimal.valueOf(packageEntity.getCnyAmount()));
        order.setPaymentMethod(RechargeOrderEntity.PaymentMethod.fromCode(request.getPaymentMethod()));
        
        // 4. 保存订单
        order = rechargeOrderRepository.save(order);
        
        
        // 6. 构建响应
        RechargeResponseVO response = new RechargeResponseVO();
        response.setOrderId(order.getOrderNo());
        response.setCoinAmount(order.getCoinAmount());
        response.setBonusCoins(order.getBonusCoins());
        
        
        return response;
    }
    
    @Override
    @Transactional
    public boolean handlePaymentCallback(String orderNo, String transactionNo) {
        // 1. 查找订单 - 这里同样用Optional包裹
        RechargeOrderEntity order = rechargeOrderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在"));
        
        // 2. 验证订单状态
        if (order.getOrderStatus() != RechargeOrderEntity.OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单状态不正确");
        }
        
        // 3. 验证订单是否过期
        if (order.getExpireAt().isBefore(LocalDateTime.now())) {
            rechargeOrderRepository.updateOrderStatus(orderNo, RechargeOrderEntity.OrderStatus.EXPIRED);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "订单已过期");
        }
        
        // 4. 标记订单为已支付
        int updated = rechargeOrderRepository.markAsPaid(orderNo, transactionNo, LocalDateTime.now());
        if (updated == 0) {
            return false;
        }
        
        // 5. 给用户增加币数
        int totalCoins = order.getCoinAmount() + order.getBonusCoins();
        userRepository.addCoins(order.getUserId(), totalCoins);
        
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public RechargeOrderEntity.OrderStatus checkOrderStatus(String orderNo, Integer userId) {
        RechargeOrderEntity order = rechargeOrderRepository.findByOrderNoAndUserId(orderNo, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "订单不存在"));
        return order.getOrderStatus();
    }
}