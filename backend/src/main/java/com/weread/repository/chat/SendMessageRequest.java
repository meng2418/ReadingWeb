package com.weread.repository.chat;

import org.springframework.stereotype.Repository;
import lombok.Data;

@Repository
@Data
public class SendMessageRequest {

    private Long conversationId;

    /**
     * text/image/book
     */
    private String messageType;

    /**
     * 文本内容 or 图片URL
     */
    private String content;

    /**
     * 分享书籍
     */
    private Integer bookId;
}