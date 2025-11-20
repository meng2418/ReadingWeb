package com.weread.repository.asset;

import com.weread.entity.asset.MemberPackageEntity;
import com.weread.entity.asset.RechargePackageEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface MemberPackageRepository extends JpaRepository<MemberPackageEntity, Long> {
    // 查找所有在售的会员套餐
    List<MemberPackageEntity> findAllAvailable();
    
    // 假设一个方法用于查找充值套餐
    Optional<RechargePackageEntity> findRechargePackageById(Long packageId);
}
