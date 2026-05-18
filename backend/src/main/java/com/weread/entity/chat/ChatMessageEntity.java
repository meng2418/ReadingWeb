package com.weread.entity.chat;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.weread.entity.book.BookEntity;

@Data
@Entity
@Table(name = "chat_message")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private ChatConversationEntity conversation;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "message_type")
    private String messageType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @Column(name = "is_recalled")
    private Boolean isRecalled = false;

    private LocalDateTime recalledAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
