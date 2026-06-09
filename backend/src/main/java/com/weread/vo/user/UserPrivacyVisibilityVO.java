package com.weread.vo.user;

import lombok.Data;

import java.util.List;

@Data
public class UserPrivacyVisibilityVO {
    private Boolean bookshelf = true;
    private Boolean readingStats = true;
    private Boolean highlights = true;
    private Boolean thoughts = true;
    private Boolean bookReviews = true;
    private Boolean followers = true;
    private Boolean following = true;
    private List<String> updated;
}
