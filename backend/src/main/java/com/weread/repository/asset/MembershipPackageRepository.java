package com.weread.repository.asset;

import com.weread.entity.asset.MembershipPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPackageRepository extends JpaRepository<MembershipPackageEntity, Integer> {
    
    /**
     * 查询所有启用的会员套餐，按显示顺序排序
     */
    List<MembershipPackageEntity> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * 查询热门推荐的会员套餐
     */
    List<MembershipPackageEntity> findByIsActiveTrueAndIsHotTrueOrderByDisplayOrderAsc();
    
    /**
     * 根据套餐ID查询启用的套餐
     */
    Optional<MembershipPackageEntity> findByPackageIdAndIsActiveTrue(Integer packageId);
    
    /**
     * 根据时长类型查询套餐
     */
    List<MembershipPackageEntity> findByDurationTypeAndIsActiveTrueOrderByDisplayOrderAsc(String durationType);
    
    /**
     * 查询指定ID列表的套餐
     */
    @Query("SELECT m FROM MembershipPackageEntity m WHERE m.packageId IN :ids AND m.isActive = true")
    List<MembershipPackageEntity> findActivePackagesByIds(@Param("ids") List<Integer> ids);
}