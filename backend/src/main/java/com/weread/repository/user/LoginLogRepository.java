package com.weread.repository.user;

import com.weread.entity.user.LoginLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLogEntity, Long> {

    /**
     * 根据用户ID查询登录日志，并按登录时间降序排列（最新日志在前）。
     */
    Page<LoginLogEntity> findByUserIdOrderByLoginTimeDesc(Long userId, Pageable pageable);
}