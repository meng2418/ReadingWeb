package com.weread.repository.asset;

import com.weread.entity.asset.CoinAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CoinAccountRepository extends JpaRepository<CoinAccountEntity, Long> {
    Optional<CoinAccountEntity> findByUserId(Long userId);
}