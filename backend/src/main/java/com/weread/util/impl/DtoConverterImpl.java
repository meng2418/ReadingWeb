package com.weread.util.impl; // 假设放在 util.impl 包下

import com.weread.util.DtoConverter;
import com.weread.entity.asset.AssetEntity; 
import com.weread.entity.asset.MemberEntity;
import com.weread.entity.asset.MemberPackageEntity;
import com.weread.entity.asset.OrderEntity;
import com.weread.entity.asset.RechargeLogEntity;
import com.weread.entity.asset.RechargePackageEntity;
import com.weread.entity.asset.ExpenseLogEntity;
import com.weread.entity.user.UserEntity;
import com.weread.entity.user.LoginLogEntity;
import com.weread.dto.asset.CoinBalanceVO;
import com.weread.dto.asset.MemberStatusVO;
import com.weread.dto.asset.MemberPackageVO;
import com.weread.dto.asset.OrderStatusVO;
import com.weread.dto.asset.RechargeLogVO;
import com.weread.dto.asset.ExpenseLogVO;
import com.weread.dto.user.LoginLogVO;
import com.weread.dto.user.UserDetailVO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // 标记为 Spring 组件
public class DtoConverterImpl implements DtoConverter {

    // --- 用户/安全相关 ---
    @Override
    public LoginLogVO toLoginLogVO(LoginLogEntity entity) {
        // 实际：使用 BeanUtils 或 MapStruct/ModelMapper 进行属性映射
        return new LoginLogVO(
            entity.getId(),
            entity.getLoginTime(),
            entity.getIpAddress(),
            entity.getDevice(),
            entity.getStatus()
        );
    }
    
    @Override
    public UserDetailVO toUserDetailVO(UserEntity entity) {
        // 示例：这里需要映射所有 UserDetailVO 字段
        return new UserDetailVO(
            entity.getId(),
            entity.getUsername(),
            entity.getAvatar(),
            entity.getBio(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getCreatedAt(),
            null, // isMember - 需从 MemberEntity 获取
            null, // memberEndDate
            null  // coins - 需从 AssetEntity 获取
        );
    }

    // --- 资产/会员相关 ---
    @Override
    public MemberStatusVO toMemberStatusVO(MemberEntity entity) {
        return new MemberStatusVO(
            entity.getEndDate().isAfter(LocalDateTime.now()), // 检查是否未过期
            entity.getLevel(),
            entity.getEndDate()
        );
    }
    
    @Override
    public MemberPackageVO toMemberPackageVO(MemberPackageEntity entity) {
        return new MemberPackageVO(
            entity.getId(),
            entity.getName(),
            entity.getDurationDays(),
            entity.getPriceCents()
        );
    }

    @Override
    public CoinBalanceVO toCoinBalanceVO(AssetEntity entity) {
        return new CoinBalanceVO(entity.getCoins());
    }

    // --- 订单/充值相关 ---
    
    // 订单创建 helper 1 (会员)
    @Override
    public OrderEntity toOrderEntity(Long userId, MemberPackageEntity packageEntity, String paymentMethod) {
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setAmountCents(packageEntity.getPriceCents());
        order.setPaymentMethod(paymentMethod);
        order.setDescription(packageEntity.getName());
        order.setCreatedAt(LocalDateTime.now());
        order.setPackageId(packageEntity.getId());
        // ... 其他属性
        return order;
    }

    // 订单创建 helper 2 (充值)
    @Override
    public OrderEntity toOrderEntity(Long userId, RechargePackageEntity packageEntity, String paymentMethod) {
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setAmountCents(packageEntity.getPriceCents());
        order.setPaymentMethod(paymentMethod);
        order.setDescription(packageEntity.getName());
        order.setCreatedAt(LocalDateTime.now());
        order.setPackageId(packageEntity.getId());
        // ... 其他属性
        return order;
    }

    @Override
    public OrderStatusVO toOrderStatusVO(OrderEntity entity) {
        return new OrderStatusVO(
            entity.getStatus(),
            entity.getAmountCents(),
            entity.getCreatedAt()
        );
    }
    
    @Override
    public RechargeLogVO toRechargeLogVO(RechargeLogEntity entity) {
         // 假设 RechargeLogEntity 包含所有必要字段
         return new RechargeLogVO(
             entity.getId(),
             entity.getAmountCents(),
             entity.getAcquiredCoins(),
             entity.getRechargeTime(),
             entity.getStatus()
         );
    }

    @Override
    public ExpenseLogVO toExpenseLogVO(ExpenseLogEntity entity) {
         // 假设 ExpenseLogEntity 包含所有必要字段
         return new ExpenseLogVO(
             entity.getId(),
             entity.getDescription(),
             entity.getSpentCoins(),
             entity.getExpenseTime()
         );
    }
}