package com.weread.service.asset;

import com.weread.vo.asset.RechargePackageVO;
import java.util.List;

public interface RechargePackageService {
    
    /**
     * 获取所有充值套餐列表
     */
    List<RechargePackageVO> getRechargePackages();
}