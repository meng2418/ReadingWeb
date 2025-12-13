package com.weread.repository.asset;

import com.weread.entity.asset.CoinTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinTransactionRepository extends JpaRepository<CoinTransactionEntity, Long> {}