package com.weread.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weread.entity.user.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhone(String phone);
    Optional<UserEntity> findByEmail(String receiverEmail);
    boolean existsByPhone(String phone);
}
