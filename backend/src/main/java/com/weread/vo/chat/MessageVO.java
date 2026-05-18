package com.weread.vo.chat;

import java.time.LocalDateTime;

import com.weread.vo.book.BookVO;

import lombok.Data;

@Data
public class MessageVO {
    private Long messageId; // 消息ID
    private Long conversationId; // 会话ID
    private Integer senderId; // 发送者ID
    private String messageType; // 消息类型 text/book
    private String content; // 消息内容
    private BookVO book; // 书籍信息（如果是书籍消息）
    private Boolean isRecalled; // 是否撤回
    private LocalDateTime createdAt;// 发送时间
}
