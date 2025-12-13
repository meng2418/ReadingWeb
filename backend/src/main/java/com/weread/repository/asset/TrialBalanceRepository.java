package com.weread.repository.asset;

import com.weread.entity.asset.TrialBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TrialBalanceRepository extends JpaRepository<TrialBalanceEntity, Long> {
    Optional<TrialBalanceEntity> findByUserId(Long userId);
}