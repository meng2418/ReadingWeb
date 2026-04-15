package com.weread.vo.reader;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiChatSendMessageResponseVO {
    private AiChatMessageVO userMessage;
    private AiChatMessageVO assistantMessage;
}
