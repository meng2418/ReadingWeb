package com.weread.entity.chat;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_message")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @Column(name = "message_type")
    private String messageType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "book_title")
    private String bookTitle;

    @Column(name = "book_cover")
    private String bookCover;

    @Column(name = "book_author")
    private String bookAuthor;

    @Column(name = "book_description")
    private String bookDescription;

    @Column(name = "is_recalled")
    private Boolean isRecalled = false;

    @Column(name = "is_withdrawn")
    private Boolean isWithdrawn = false;

    @Column(name = "recalled_at")
    private LocalDateTime recalledAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (sendTime == null) {
            sendTime = now;
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (isRecalled == null) {
            isRecalled = false;
        }
        if (isWithdrawn == null) {
            isWithdrawn = false;
        }
    }
}
