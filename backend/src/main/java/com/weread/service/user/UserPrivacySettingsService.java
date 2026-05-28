package com.weread.service.user;

import com.weread.dto.user.UpdateUserPrivacySettingsDTO;
import com.weread.vo.user.UserPrivacyVisibilityVO;

public interface UserPrivacySettingsService {

    UserPrivacyVisibilityVO getVisibility(Integer userId);

    UserPrivacyVisibilityVO updateVisibility(Integer userId, UpdateUserPrivacySettingsDTO dto);
}
