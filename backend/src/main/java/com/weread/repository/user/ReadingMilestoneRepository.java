package com.weread.repository.user;

import com.weread.entity.user.ReadingMilestoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingMilestoneRepository extends JpaRepository<ReadingMilestoneEntity, Long> {
    
    Optional<ReadingMilestoneEntity> findTopByUserIdAndMilestoneTypeAndIsLatestTrueOrderByTargetCountDesc(
            Integer userId, String milestoneType);
    
    List<ReadingMilestoneEntity> findByUserIdAndMilestoneTypeOrderByTargetCountDesc(
            Integer userId, String milestoneType);
    
    boolean existsByUserIdAndMilestoneTypeAndTargetCount(
            Integer userId, String milestoneType, Integer targetCount);
    
    @Modifying
    @Query("UPDATE ReadingMilestoneEntity m SET m.isLatest = false WHERE m.userId = :userId AND m.milestoneType = :milestoneType")
    void markAllAsNotLatest(@Param("userId") Integer userId, @Param("milestoneType") String milestoneType);
    
    @Query("SELECT COUNT(m) > 0 FROM ReadingMilestoneEntity m WHERE m.userId = :userId AND m.milestoneType = :milestoneType AND m.targetCount = :targetCount")
    boolean checkMilestoneExists(@Param("userId") Integer userId, 
                               @Param("milestoneType") String milestoneType, 
                               @Param("targetCount") Integer targetCount);
}