package com.weread.service.reader;

import com.weread.vo.reader.AiChatHistoryVO;
import com.weread.vo.reader.AiChatSendMessageResponseVO;

public interface AiChatService {

    AiChatHistoryVO getHistory(Integer userId, Integer bookId, Integer limit, Integer cursor);

    AiChatSendMessageResponseVO sendMessage(Integer userId, Integer bookId, String message);
}
