package com.weread.repository.user;

import com.weread.entity.user.UserStatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatRepository extends JpaRepository<UserStatEntity, Long> {

    /**
     * 根据用户ID查找统计记录
     */
    Optional<UserStatEntity> findByUserId(Long userId);
}