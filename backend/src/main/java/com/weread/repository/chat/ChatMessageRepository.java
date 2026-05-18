package com.weread.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weread.entity.chat.ChatConversationEntity;
import com.weread.entity.chat.ChatMessageEntity;
import java.util.List;

@Repository
public interface ChatMessageRepository
                extends JpaRepository<ChatMessageEntity, Long> {

        List<ChatMessageEntity> findByConversationConversationIdOrderByCreatedAtDesc(
                        Long conversationId);

        List<ChatMessageEntity> findByConversationOrderByCreatedAtAsc(ChatConversationEntity conv);

        // 查找某会话最后一条消息
        ChatMessageEntity findTopByConversationOrderByCreatedAtDesc(
                        ChatConversationEntity conversation);
}
