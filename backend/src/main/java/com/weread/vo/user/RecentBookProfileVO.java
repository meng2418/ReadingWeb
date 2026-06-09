package com.weread.vo.user;

import lombok.Data;

@Data
public class RecentBookProfileVO {
    private Integer bookId;
    private String title;
    private String cover;
    private String readingStatus;
}
