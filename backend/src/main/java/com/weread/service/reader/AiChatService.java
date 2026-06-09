package com.weread.service.reader;

import com.weread.vo.reader.AiChatHistoryVO;
import com.weread.vo.reader.AiChatSendMessageResponseVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiChatService {

    AiChatHistoryVO getHistory(Integer userId, Integer bookId, Integer limit, Integer cursor);

    AiChatSendMessageResponseVO sendMessage(Integer userId, Integer bookId, String message);

    /**
     * 流式发送消息：先持久化用户消息，SSE 逐块返回 AI 回复，完成后保存 assistant 消息
     */
    SseEmitter streamMessage(Integer userId, Integer bookId, String message);
}
