package com.weread.service.impl.asset;

import com.weread.entity.asset.MembershipPackageEntity;
import com.weread.repository.asset.MembershipPackageRepository;
import com.weread.service.asset.MembershipService;
import com.weread.vo.asset.MembershipPackageVO;
import com.weread.vo.asset.MembershipPaymentInfoVO;
import com.weread.dto.asset.PurchaseMembershipDTO;
import com.weread.vo.asset.PurchaseResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    
    private final MembershipPackageRepository membershipPackageRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<MembershipPackageVO> getMembershipPackages(String showType) {
        List<MembershipPackageEntity> packages;
        
        if ("hot".equals(showType)) {
            packages = membershipPackageRepository.findByIsActiveTrueAndIsHotTrueOrderByDisplayOrderAsc();
        } else {
            packages = membershipPackageRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        }
        
        return packages.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public MembershipPaymentInfoVO getPaymentInfo(Integer packageId, Integer userId) {
        MembershipPackageEntity packageEntity = membershipPackageRepository
                .findByPackageIdAndIsActiveTrue(packageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "套餐不存在或已下架"));
        
        MembershipPaymentInfoVO vo = new MembershipPaymentInfoVO();
        vo.setPackageId(packageEntity.getPackageId());
        vo.setPackageName(packageEntity.getPackageName());
        vo.setDurationDays(packageEntity.getDurationDays());
        vo.setOriginalAmount(packageEntity.getOriginalPrice());
        vo.setDiscountAmount(packageEntity.getDiscountPrice());
        
        // 支付方式
        vo.setPaymentMethods(Arrays.asList("wechat", "alipay", "balance", "card"));
        
        return vo;
    }
    
    @Override
    @Transactional
    public PurchaseResultVO purchaseMembership(PurchaseMembershipDTO purchaseDTO, Integer userId) {
        // 验证套餐
        MembershipPackageEntity packageEntity = membershipPackageRepository
                .findByPackageIdAndIsActiveTrue(purchaseDTO.getPackageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "套餐不存在或已下架"));
        
        // 生成订单（简化）
        String orderNo = "M" + System.currentTimeMillis();
        
        // 返回结果
        PurchaseResultVO result = new PurchaseResultVO();
        result.setOrderNo(orderNo);
        result.setDurationType(packageEntity.getDurationType());
        result.setDurationDays(packageEntity.getDurationDays());
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object checkUserMembership(Integer userId) {
        // 简化：这里应该查询用户会员表
        return null;
    }
    
    private MembershipPackageVO convertToVO(MembershipPackageEntity entity) {
        MembershipPackageVO vo = new MembershipPackageVO();
        vo.setPackageId(entity.getPackageId());
        vo.setPackageName(entity.getPackageName());
        vo.setDurationDays(entity.getDurationDays());
        vo.setOriginalPrice(entity.getOriginalPrice());
        vo.setDiscountPrice(entity.getDiscountPrice());
        
        return vo;
    }
}