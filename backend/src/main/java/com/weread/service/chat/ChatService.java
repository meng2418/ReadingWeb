package com.weread.service.chat;

import com.weread.dto.chat.SendMessageRequest;
import com.weread.entity.chat.ChatMessageEntity;
import com.weread.vo.chat.ConversationVO;
import com.weread.vo.chat.MessageVO;

import java.util.List;

public interface ChatService {

    ChatMessageEntity sendMessage(
            Integer currentUserId,
            SendMessageRequest request);

    List<MessageVO> getMessages(Long conversationId);

    void recallMessage(Long messageId, Integer currentUserId);

    List<ConversationVO> getConversations(Integer currentUserId);
}
