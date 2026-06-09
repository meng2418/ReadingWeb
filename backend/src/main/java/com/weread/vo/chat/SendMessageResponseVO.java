package com.weread.vo.chat;

import lombok.Data;

@Data
public class SendMessageResponseVO {
    private MessageVO message;
    private Boolean showTimestamp;
}
