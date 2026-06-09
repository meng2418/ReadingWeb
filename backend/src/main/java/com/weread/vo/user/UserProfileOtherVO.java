package com.weread.vo.user;

import com.weread.vo.user.UserProfileVO.ReadingStatsVO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class UserProfileOtherVO {
    private String avatar;
    private String username;
    private String bio;
    private Integer followingCount;
    private Integer followerCount;
    private Integer postCount;
    private Boolean isMember;
    private ReadingStatsVO readingStats;
    private Integer consecutiveReadingDays;
    private UserPrivacyVisibilityVO visibility;
    private ProfileBookshelfPageVO bookshelf;
    private List<HighlightProfileVO> highlights = Collections.emptyList();
    private List<ThoughtProfileVO> thoughts = Collections.emptyList();
    private List<BookReviewProfileVO> bookReviews = Collections.emptyList();
    private Boolean isFollowing = false;
    private Boolean isFollower = false;
    private Boolean isSelf = false;
}
