package com.weread.vo.user;

import lombok.Data;
import java.util.List;

@Data
public class FollowListVO {
    private List<FollowUserVO> users; // 包含关注状态的VO列表
    private long totalElements;
    private int totalPages;
    private int currentPage;
}