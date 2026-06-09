package com.weread.service.impl.user;

import com.weread.dto.user.UpdateUserPrivacySettingsDTO;
import com.weread.entity.user.UserPrivacySettingsEntity;
import com.weread.repository.user.UserPrivacySettingsRepository;
import com.weread.service.user.UserPrivacySettingsService;
import com.weread.vo.user.UserPrivacyVisibilityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPrivacySettingsServiceImpl implements UserPrivacySettingsService {

    private final UserPrivacySettingsRepository privacySettingsRepository;

    @Override
    @Transactional(readOnly = true)
    public UserPrivacyVisibilityVO getVisibility(Integer userId) {
        return privacySettingsRepository.findByUserId(userId)
                .map(this::toVisibilityVO)
                .orElseGet(this::defaultVisibility);
    }

    @Override
    @Transactional
    public UserPrivacyVisibilityVO updateVisibility(Integer userId, UpdateUserPrivacySettingsDTO dto) {
        UserPrivacySettingsEntity entity = privacySettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultEntity(userId));

        List<String> updatedFields = new ArrayList<>();

        if (dto.getBookshelf() != null) {
            entity.setBookshelf(dto.getBookshelf());
            updatedFields.add("bookshelf");
        }
        if (dto.getReadingStats() != null) {
            entity.setReadingStats(dto.getReadingStats());
            updatedFields.add("readingStats");
        }
        if (dto.getHighlights() != null) {
            entity.setHighlights(dto.getHighlights());
            updatedFields.add("highlights");
        }
        if (dto.getThoughts() != null) {
            entity.setThoughts(dto.getThoughts());
            updatedFields.add("thoughts");
        }
        if (dto.getBookReviews() != null) {
            entity.setBookReviews(dto.getBookReviews());
            updatedFields.add("bookReviews");
        }
        if (dto.getFollowers() != null) {
            entity.setFollowers(dto.getFollowers());
            updatedFields.add("followers");
        }
        if (dto.getFollowing() != null) {
            entity.setFollowing(dto.getFollowing());
            updatedFields.add("following");
        }

        UserPrivacyVisibilityVO vo = toVisibilityVO(privacySettingsRepository.save(entity));
        vo.setUpdated(updatedFields);
        return vo;
    }

    private UserPrivacySettingsEntity createDefaultEntity(Integer userId) {
        UserPrivacySettingsEntity entity = new UserPrivacySettingsEntity();
        entity.setUserId(userId);
        return entity;
    }

    private UserPrivacyVisibilityVO defaultVisibility() {
        return new UserPrivacyVisibilityVO();
    }

    private UserPrivacyVisibilityVO toVisibilityVO(UserPrivacySettingsEntity entity) {
        UserPrivacyVisibilityVO vo = new UserPrivacyVisibilityVO();
        vo.setBookshelf(entity.getBookshelf());
        vo.setReadingStats(entity.getReadingStats());
        vo.setHighlights(entity.getHighlights());
        vo.setThoughts(entity.getThoughts());
        vo.setBookReviews(entity.getBookReviews());
        vo.setFollowers(entity.getFollowers());
        vo.setFollowing(entity.getFollowing());
        return vo;
    }
}
