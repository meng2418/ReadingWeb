package com.weread.dto.user;

import lombok.Data;

@Data
public class UpdateUserPrivacySettingsDTO {
    private Boolean bookshelf;
    private Boolean readingStats;
    private Boolean highlights;
    private Boolean thoughts;
    private Boolean bookReviews;
    private Boolean followers;
    private Boolean following;
}
