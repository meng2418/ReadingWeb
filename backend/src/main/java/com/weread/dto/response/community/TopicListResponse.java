package com.weread.dto.response.community;

import com.weread.vo.community.TopicVO;
import lombok.Data;
import java.util.List;

@Data
public class TopicListResponse {
    private List<TopicVO> items;
    private boolean hasMore;
    private Integer nextCursor;
}