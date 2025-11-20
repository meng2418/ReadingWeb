package com.weread.util;

// 假设导入所有需要转换的 Entity 和 DTO/VO
import com.weread.entity.user.LoginLogEntity;
import com.weread.entity.asset.AssetEntity;
import com.weread.entity.asset.OrderEntity;
import com.weread.entity.asset.RechargePackageEntity;
import com.weread.entity.asset.RechargeLogEntity;
import com.weread.entity.asset.ExpenseLogEntity;
import com.weread.entity.asset.MemberEntity;
import com.weread.entity.asset.MemberPackageEntity;
import com.weread.entity.user.UserEntity;
import com.weread.dto.asset.MemberPackageVO;
import com.weread.dto.asset.MemberStatusVO;
import com.weread.dto.asset.CoinBalanceVO;
import com.weread.dto.asset.OrderStatusVO;
import com.weread.dto.asset.RechargeLogVO;
import com.weread.dto.asset.ExpenseLogVO;
import com.weread.dto.user.LoginLogVO;
import com.weread.dto.user.UserDetailVO;


/**
 * DtoConverter - 实体(Entity)与视图对象(VO)之间的转换工具。
 */
public interface DtoConverter {

    // --- 用户/安全相关 ---
    LoginLogVO toLoginLogVO(LoginLogEntity entity);
    UserDetailVO toUserDetailVO(UserEntity entity);

    // --- 资产/会员相关 ---
    MemberStatusVO toMemberStatusVO(MemberEntity entity);
    MemberPackageVO toMemberPackageVO(MemberPackageEntity entity);
    CoinBalanceVO toCoinBalanceVO(AssetEntity entity);

    // --- 订单/充值相关 ---
    OrderEntity toOrderEntity(Long userId, MemberPackageEntity packageEntity, String paymentMethod);
    OrderEntity toOrderEntity(Long userId, RechargePackageEntity packageEntity, String paymentMethod);
    OrderStatusVO toOrderStatusVO(OrderEntity entity);
    RechargeLogVO toRechargeLogVO(RechargeLogEntity entity);
    ExpenseLogVO toExpenseLogVO(ExpenseLogEntity entity);
}