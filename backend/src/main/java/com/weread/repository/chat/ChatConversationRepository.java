package com.weread.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.weread.entity.chat.ChatConversationEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository
                extends JpaRepository<ChatConversationEntity, Long> {

        Optional<ChatConversationEntity> findByUser1IdAndUser2Id(Integer currentUserId, Integer targetUserId);

        List<ChatConversationEntity> findByUser1IdOrUser2IdOrderByLastMessageTimeDesc(
                        Integer user1Id,
                        Integer user2Id);

        List<ChatConversationEntity> findByUser1IdOrUser2Id(Integer uid1, Integer uid2);

}