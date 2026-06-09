package com.weread.vo.chat;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageVO {
    private Long messageId;
    private Long conversationId;
    private Integer senderId;
    private String senderAvatar;
    private String senderName;
    private Integer receiverId;
    private String messageType;
    private String content;
    private BookInfo bookInfo;
    private LocalDateTime sendTime;
    private Boolean isWithdrawn;
    private Boolean showTimestamp;

    @Data
    public static class BookInfo {
        private Integer bookId = 0;
        private String bookTitle = "";
        private String cover = "";
        private String authorName = "";
        private String description = "";

        public static BookInfo empty() {
            return new BookInfo();
        }
    }
}
