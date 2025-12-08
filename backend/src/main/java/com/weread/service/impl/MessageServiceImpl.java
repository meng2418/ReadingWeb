package com.weread.service.impl;

import com.weread.entity.community.MessageEntity;
import com.weread.repository.community.MessageRepository;
import com.weread.service.community.MessageService;
import com.weread.vo.community.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional // 确保消息保存操作在一个事务内
    public void pushMessage(Long userId, String type, String title, String content, Long relatedId) {
        MessageEntity message = new MessageEntity();
        message.setUserId(userId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setRelatedId(relatedId);
        message.setIsRead(false); // 新消息默认为未读

        messageRepository.save(message);
        logger.info("成功向用户 {} 推送消息：{}", userId, title);
    }

    @Override
    public Page<MessageVO> getMessages(Long userId, Pageable pageable) {
        // 1. 查询 Entity 分页结果
        Page<MessageEntity> messageEntities = messageRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        // 2. 转换为 VO 分页结果 (假设有一个 MessageMapper 或转换逻辑)
        return messageEntities.map(this::convertToMessageVO);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return messageRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional // @Modifying 操作需要事务
    public void markMessageAsRead(Long userId, Long messageId) {
        int updatedRows = messageRepository.markAsRead(messageId, userId);
        
        if (updatedRows == 0) {
            // 如果更新行数为 0，可能消息不存在，或者不属于该用户
            logger.warn("用户 {} 尝试标记消息 {} 为已读，但操作失败或消息已读。", userId, messageId);
            // 可以选择抛出异常，也可以选择静默处理
        }
    }

    @Override
    @Transactional // @Modifying 操作需要事务
    public void markAllMessagesAsRead(Long userId) {
        int updatedRows = messageRepository.markAllAsRead(userId);
        logger.info("用户 {} 标记了 {} 条消息为已读。", userId, updatedRows);
    }
    
    // 辅助转换方法 (您需要自行创建 MessageVO)
    private MessageVO convertToMessageVO(MessageEntity entity) {
        MessageVO vo = new MessageVO();
        vo.setMessageId(entity.getMessageId());
        vo.setType(entity.getType());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setIsRead(entity.getIsRead());
        vo.setRelatedId(entity.getRelatedId());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }
}