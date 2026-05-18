package com.weread.entity.chat;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "chat_conversation", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user1_id", "user2_id" })
})
public class ChatConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conversationId;

    @Column(name = "user1_id", nullable = false)
    private Integer user1Id;

    @Column(name = "user2_id", nullable = false)
    private Integer user2Id;

    @Column(name = "last_message_content")
    private String lastMessageContent;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}