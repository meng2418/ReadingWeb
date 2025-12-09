package com.weread.service.community;

import com.weread.vo.community.MessageVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    /**
     * 推送一条新消息（供其他 Service 调用）
     */
    void pushMessage(Long userId, String type, String title, String content, Long relatedId);

    /**
     * 分页获取用户的消息列表
     */
    Page<MessageVO> getMessages(Long userId, Pageable pageable);

    /**
     * 获取用户未读消息总数
     */
    long getUnreadCount(Long userId);

    /**
     * 标记单条消息为已读
     */
    void markMessageAsRead(Long userId, Integer messageId);

    /**
     * 标记用户所有消息为已读
     */
    void markAllMessagesAsRead(Long userId);
}