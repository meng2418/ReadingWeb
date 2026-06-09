package com.weread.vo.chat;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConversationVO {
    private Long conversationId; // 会话ID
    private Integer targetUserId; // 对方用户ID
    private String nickname; // 对方昵称
    private String avatar; // 对方头像
    private String lastMessageContent; // 最后一条消息
    private LocalDateTime lastMessageTime; // 最后消息时间
}
