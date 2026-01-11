package com.weread.repository.asset;

import com.weread.entity.asset.RechargePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RechargePackageRepository extends JpaRepository<RechargePackageEntity, Integer> {
    
    /**
     * 获取所有启用的充值套餐，按显示顺序排序
     */
    List<RechargePackageEntity> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * 根据ID获取充值套餐
     */
    Optional<RechargePackageEntity> findByPackageIdAndIsActiveTrue(Integer packageId);
}
