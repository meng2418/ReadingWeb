package com.weread.vo.reader;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiChatHistoryVO {
    private String bookTitle;
    private List<AiChatMessageVO> messages;
    private boolean hasMore;
    private Integer nextCursor;
}
