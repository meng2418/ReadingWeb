package com.weread.vo.user;

import lombok.Data;
@Data
public class UserProfileVO {

    private String avatar;
    private String username;
    private String bio;

    private Integer followingCount;
    private Integer followerCount;
    private Integer postCount;

    private Integer memberCardCount;
    private Integer coinCount;
    private Boolean isMember;
    private Integer memberExpireDays;

    private ReadingStatsVO readingStats;

    private Integer consecutiveReadingDays;
}
