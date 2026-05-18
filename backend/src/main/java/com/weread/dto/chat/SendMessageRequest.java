package com.weread.dto.chat;

import lombok.Data;

@Data
public class SendMessageRequest {

    private Integer receiverId;

    private String messageType;

    private String content;

    private BookInfo bookInfo;

    @Data
    public static class BookInfo {

        private Integer bookId;

        private String bookTitle;

        private String cover;

        private String authorName;

        private String description;
    }
}
