package com.weread.vo.community;

import lombok.Data;
import java.util.List;

@Data
public class CommentListVO {
    private List<CommentVO> comments;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private boolean isLast;
}