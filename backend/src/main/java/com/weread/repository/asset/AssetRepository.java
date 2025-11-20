package com.weread.repository.asset;
import com.weread.entity.asset.*;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// ------------------------------------
// 资产/会员 Repository
// ------------------------------------

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {
Optional<AssetEntity> findByUserId(Long userId);
}
