package com.weread.repository.user;
import com.weread.entity.user.ReadingRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReadingRewardRepository extends JpaRepository<ReadingRewardEntity, Long> {
    
    boolean existsByUserIdAndRewardDateAndRewardType(Integer userId, LocalDate rewardDate, String rewardType);
    
    List<ReadingRewardEntity> findByUserIdOrderByRewardDateDesc(Integer userId);
}