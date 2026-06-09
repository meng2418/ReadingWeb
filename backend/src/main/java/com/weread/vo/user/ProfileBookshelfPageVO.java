package com.weread.vo.user;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ProfileBookshelfPageVO {
    private Integer total = 0;
    private Integer page = 1;
    private Integer limit = 10;
    private Boolean hasMore = false;
    private List<RecentBookProfileVO> items = Collections.emptyList();
}
