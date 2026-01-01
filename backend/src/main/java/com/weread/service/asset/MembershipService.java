package com.weread.service.asset;

import com.weread.vo.asset.MembershipPackageVO;
import com.weread.vo.asset.MembershipPaymentInfoVO;
import com.weread.dto.asset.PurchaseMembershipDTO;
import com.weread.vo.asset.PurchaseResultVO;

import java.util.List;

public interface MembershipService {
    
    /**
     * 获取所有会员套餐列表
     * @param showType 显示类型：all-全部，active-仅启用，hot-热门
     * @return 套餐列表
     */
    List<MembershipPackageVO> getMembershipPackages(String showType);
    
    /**
     * 获取会员支付页信息
     * @param packageId 套餐ID
     * @param userId 用户ID
     * @return 支付信息
     */
    MembershipPaymentInfoVO getPaymentInfo(Integer packageId, Integer userId);
    
    /**
     * 开通会员
     * @param purchaseDTO 购买信息
     * @param userId 用户ID
     * @return 购买结果
     */
    PurchaseResultVO purchaseMembership(PurchaseMembershipDTO purchaseDTO, Integer userId);
    
    /**
     * 检查用户会员状态
     * @param userId 用户ID
     * @return 会员到期时间等信息
     */
    Object checkUserMembership(Integer userId);
}