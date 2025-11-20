package com.weread.repository.asset;

import com.weread.entity.asset.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUserId(Long userId);
}
