package com.weread.repository.user;

import com.weread.entity.user.UserPrivacySettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPrivacySettingsRepository extends JpaRepository<UserPrivacySettingsEntity, Integer> {

    Optional<UserPrivacySettingsEntity> findByUserId(Integer userId);
}
