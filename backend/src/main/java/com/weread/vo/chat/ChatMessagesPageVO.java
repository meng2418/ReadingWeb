package com.weread.vo.chat;

import com.weread.vo.user.UserSimpleVO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ChatMessagesPageVO {
    private UserSimpleVO targetUser;
    private UserSimpleVO currentUser;
    private List<MessageVO> messages = Collections.emptyList();
    private Boolean hasMore = false;
    private Integer nextCursor = 0;
}
