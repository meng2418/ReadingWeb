package com.weread.service.impl.asset;

import com.weread.entity.asset.RechargePackageEntity;
import com.weread.repository.asset.RechargePackageRepository;
import com.weread.service.asset.RechargePackageService;
import com.weread.vo.asset.RechargePackageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RechargePackageServiceImpl implements RechargePackageService {
    
    private final RechargePackageRepository rechargePackageRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<RechargePackageVO> getRechargePackages() {
        // 查询所有启用的充值套餐，按显示顺序排序
        List<RechargePackageEntity> packages = rechargePackageRepository
            .findByIsActiveTrueOrderByDisplayOrderAsc();
        
        // 转换为VO
        return packages.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }
    
    private RechargePackageVO convertToVO(RechargePackageEntity entity) {
        RechargePackageVO vo = new RechargePackageVO();
        vo.setCoinAmount(entity.getCoinAmount());
        vo.setCnyAmount(entity.getCnyAmount());
        vo.setBonusCoins(entity.getBonusCoins());
        return vo;
    }
}
