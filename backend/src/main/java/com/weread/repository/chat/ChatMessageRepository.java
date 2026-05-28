package com.weread.repository.chat;

import com.weread.entity.chat.ChatMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findByConversationIdOrderBySendTimeAsc(Long conversationId);

    List<ChatMessageEntity> findByConversationIdOrderByIdDesc(Long conversationId, Pageable pageable);

    List<ChatMessageEntity> findByConversationIdAndIdLessThanOrderByIdDesc(
            Long conversationId, Long cursor, Pageable pageable);

    ChatMessageEntity findTopByConversationIdOrderByIdDesc(Long conversationId);
}
