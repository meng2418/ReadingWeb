package com.weread.service.chat;

import com.weread.dto.chat.SendMessageRequest;
import com.weread.vo.chat.ChatMessagesPageVO;
import com.weread.vo.chat.ConversationVO;
import com.weread.vo.chat.MessageVO;
import com.weread.vo.chat.SendMessageResponseVO;

import java.util.List;

public interface ChatService {

    SendMessageResponseVO sendMessage(Integer currentUserId, SendMessageRequest request);

    List<MessageVO> getMessages(Long conversationId);

    ChatMessagesPageVO getConversationMessages(Integer currentUserId, Integer targetUserId, Long cursor, Integer limit);

    void recallMessage(Long messageId, Integer currentUserId);

    List<ConversationVO> getConversations(Integer currentUserId);
}
