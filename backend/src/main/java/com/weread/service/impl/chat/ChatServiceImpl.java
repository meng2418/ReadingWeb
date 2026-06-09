package com.weread.service.impl.chat;

import com.weread.dto.chat.SendMessageRequest;
import com.weread.entity.book.BookEntity;
import com.weread.entity.chat.ChatConversationEntity;
import com.weread.entity.chat.ChatMessageEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.book.BookRepository;
import com.weread.repository.chat.ChatConversationRepository;
import com.weread.repository.chat.ChatMessageRepository;
import com.weread.service.chat.ChatService;
import com.weread.vo.chat.ChatMessagesPageVO;
import com.weread.vo.chat.ConversationVO;
import com.weread.vo.chat.MessageVO;
import com.weread.vo.chat.SendMessageResponseVO;
import com.weread.vo.user.UserSimpleVO;
import com.weread.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public SendMessageResponseVO sendMessage(Integer senderId, SendMessageRequest request) {
        if (request.getReceiverId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "receiverId不能为空");
        }

        ChatConversationEntity conversation = findOrCreateConversation(senderId, request.getReceiverId());
        ChatMessageEntity previousMessage = messageRepository.findTopByConversationIdOrderByIdDesc(conversation.getId());

        ChatMessageEntity message = new ChatMessageEntity();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent());
        message.setIsRecalled(false);
        message.setIsWithdrawn(false);

        if ("book".equals(request.getMessageType())) {
            if (request.getBookInfo() == null || request.getBookInfo().getBookId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookId不能为空");
            }

            BookEntity book = bookRepository.findByBookId(request.getBookInfo().getBookId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "书籍不存在"));

            message.setBookId(book.getBookId());
            message.setBookTitle(book.getTitle());
            message.setBookCover(book.getCover());
            if (book.getAuthor() != null) {
                message.setBookAuthor(book.getAuthor().getAuthorName());
            }
            message.setBookDescription(book.getDescription());
        }

        ChatMessageEntity savedMessage = messageRepository.save(message);

        String lastMessage = buildPreviewText(request.getMessageType(), request.getContent());
        conversation.setLastMessageContent(lastMessage);
        conversation.setLastMessageTime(LocalDateTime.now());
        conversationRepository.save(conversation);

        boolean showTimestamp = shouldShowTimestamp(previousMessage, savedMessage);
        SendMessageResponseVO response = new SendMessageResponseVO();
        response.setMessage(toMessageVO(savedMessage, conversation.getId(), showTimestamp));
        response.setShowTimestamp(showTimestamp);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageVO> getMessages(Long conversationId) {
        List<ChatMessageEntity> entities =
                messageRepository.findByConversationIdOrderBySendTimeAsc(conversationId);
        List<MessageVO> result = new ArrayList<>();
        ChatMessageEntity previous = null;
        for (ChatMessageEntity entity : entities) {
            result.add(toMessageVO(entity, conversationId, shouldShowTimestamp(previous, entity)));
            previous = entity;
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatMessagesPageVO getConversationMessages(
            Integer currentUserId, Integer targetUserId, Long cursor, Integer limit) {
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        UserEntity currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户未登录"));

        int safeLimit = (limit == null || limit < 1) ? 20 : Math.min(limit, 50);

        ChatConversationEntity conversation = findConversation(currentUserId, targetUserId);
        ChatMessagesPageVO pageVO = new ChatMessagesPageVO();
        pageVO.setTargetUser(toUserSimpleVO(targetUser));
        pageVO.setCurrentUser(toUserSimpleVO(currentUser));
        if (conversation == null) {
            pageVO.setMessages(Collections.emptyList());
            pageVO.setHasMore(false);
            pageVO.setNextCursor(0);
            return pageVO;
        }

        Long conversationId = conversation.getId();
        PageRequest pageable = PageRequest.of(0, safeLimit + 1);
        List<ChatMessageEntity> rows = cursor == null
                ? messageRepository.findByConversationIdOrderByIdDesc(conversationId, pageable)
                : messageRepository.findByConversationIdAndIdLessThanOrderByIdDesc(
                        conversationId, cursor, pageable);

        boolean hasMore = rows.size() > safeLimit;
        if (hasMore) {
            rows = rows.subList(0, safeLimit);
        }

        List<ChatMessageEntity> sortedRows = rows.stream()
                .sorted(Comparator.comparing(ChatMessageEntity::getId))
                .toList();
        List<MessageVO> messages = new ArrayList<>();
        ChatMessageEntity previous = null;
        for (ChatMessageEntity entity : sortedRows) {
            messages.add(toMessageVO(entity, conversationId, shouldShowTimestamp(previous, entity)));
            previous = entity;
        }

        pageVO.setMessages(messages);
        pageVO.setHasMore(hasMore);
        pageVO.setNextCursor(hasMore && !rows.isEmpty()
                ? rows.get(rows.size() - 1).getId().intValue()
                : 0);
        return pageVO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationVO> getConversations(Integer userId) {
        List<ChatConversationEntity> list = conversationRepository
                .findByUser1IdOrUser2IdOrderByLastMessageTimeDesc(userId, userId);

        List<ConversationVO> result = new ArrayList<>();
        for (ChatConversationEntity conv : list) {
            ConversationVO vo = new ConversationVO();
            vo.setConversationId(conv.getId());
            vo.setLastMessageContent(conv.getLastMessageContent());
            vo.setLastMessageTime(conv.getLastMessageTime());

            Integer targetId = userId.equals(conv.getUser1Id()) ? conv.getUser2Id() : conv.getUser1Id();
            UserEntity user = userRepository.findById(targetId).orElse(null);
            if (user != null) {
                vo.setTargetUserId(user.getUserId());
                vo.setNickname(user.getUsername());
                vo.setAvatar(user.getAvatar());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public void recallMessage(Long messageId, Integer userId) {
        ChatMessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "消息不存在"));

        if (!message.getSenderId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能撤回别人的消息");
        }
        if (Boolean.TRUE.equals(message.getIsRecalled()) || Boolean.TRUE.equals(message.getIsWithdrawn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "消息已经撤回");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sentAt = message.getSendTime() != null ? message.getSendTime() : message.getCreatedAt();
        if (sentAt != null && sentAt.plusMinutes(2).isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "消息超过2分钟无法撤回");
        }

        message.setIsRecalled(true);
        message.setIsWithdrawn(true);
        message.setRecalledAt(now);
        message.setWithdrawnAt(now);
        messageRepository.save(message);

        ChatMessageEntity lastMessage = messageRepository
                .findTopByConversationIdOrderByIdDesc(message.getConversationId());
        if (lastMessage != null) {
            conversationRepository.findById(message.getConversationId()).ifPresent(conversation -> {
                String content;
                if (lastMessage.getId().equals(messageId)) {
                    content = "消息已撤回";
                } else {
                    content = buildPreviewText(lastMessage.getMessageType(), lastMessage.getContent());
                    if (Boolean.TRUE.equals(lastMessage.getIsRecalled()) || Boolean.TRUE.equals(lastMessage.getIsWithdrawn())) {
                        content = "消息已撤回";
                    }
                }
                conversation.setLastMessageContent(content);
                conversation.setLastMessageTime(now);
                conversationRepository.save(conversation);
            });
        }
    }

    private ChatConversationEntity findOrCreateConversation(Integer user1Id, Integer user2Id) {
        ChatConversationEntity conversation = findConversation(user1Id, user2Id);
        if (conversation != null) {
            return conversation;
        }

        conversation = new ChatConversationEntity();
        conversation.setUser1Id(user1Id);
        conversation.setUser2Id(user2Id);
        return conversationRepository.save(conversation);
    }

    private ChatConversationEntity findConversation(Integer user1Id, Integer user2Id) {
        return conversationRepository.findByUser1IdAndUser2Id(user1Id, user2Id)
                .orElseGet(() -> conversationRepository.findByUser1IdAndUser2Id(user2Id, user1Id).orElse(null));
    }

    private MessageVO toMessageVO(ChatMessageEntity msg, Long conversationId, boolean showTimestamp) {
        MessageVO vo = new MessageVO();
        vo.setMessageId(msg.getId());
        vo.setConversationId(conversationId);
        vo.setSenderId(msg.getSenderId());
        vo.setReceiverId(msg.getReceiverId());
        vo.setMessageType(msg.getMessageType());
        boolean withdrawn = Boolean.TRUE.equals(msg.getIsRecalled()) || Boolean.TRUE.equals(msg.getIsWithdrawn());
        vo.setContent(withdrawn ? "消息已撤回" : msg.getContent());
        vo.setIsWithdrawn(withdrawn);
        vo.setSendTime(msg.getSendTime() != null ? msg.getSendTime() : msg.getCreatedAt());
        vo.setShowTimestamp(showTimestamp);

        userRepository.findById(msg.getSenderId()).ifPresent(sender -> {
            vo.setSenderName(sender.getUsername());
            vo.setSenderAvatar(sender.getAvatar());
        });

        vo.setBookInfo(toBookInfo(msg));

        return vo;
    }

    private MessageVO.BookInfo toBookInfo(ChatMessageEntity msg) {
        MessageVO.BookInfo bookInfo = MessageVO.BookInfo.empty();
        if (msg.getBookId() == null) {
            return bookInfo;
        }
        bookInfo.setBookId(msg.getBookId());
        bookInfo.setBookTitle(msg.getBookTitle() != null ? msg.getBookTitle() : "");
        bookInfo.setCover(msg.getBookCover() != null ? msg.getBookCover() : "");
        bookInfo.setAuthorName(msg.getBookAuthor() != null ? msg.getBookAuthor() : "");
        bookInfo.setDescription(msg.getBookDescription() != null ? msg.getBookDescription() : "");
        return bookInfo;
    }

    private String buildPreviewText(String messageType, String content) {
        return switch (messageType) {
            case "image" -> "[图片]";
            case "book" -> "[分享书籍]";
            default -> content;
        };
    }

    private UserSimpleVO toUserSimpleVO(UserEntity user) {
        UserSimpleVO vo = new UserSimpleVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setBio(user.getBio());
        return vo;
    }

    private boolean shouldShowTimestamp(ChatMessageEntity previousMessage, ChatMessageEntity currentMessage) {
        if (previousMessage == null) {
            return true;
        }
        LocalDateTime previousTime = previousMessage.getSendTime() != null
                ? previousMessage.getSendTime()
                : previousMessage.getCreatedAt();
        LocalDateTime currentTime = currentMessage.getSendTime() != null
                ? currentMessage.getSendTime()
                : currentMessage.getCreatedAt();
        if (previousTime == null || currentTime == null) {
            return true;
        }
        if (!previousTime.toLocalDate().equals(currentTime.toLocalDate())) {
            return true;
        }
        return Duration.between(previousTime, currentTime).toMinutes() >= 5;
    }
}
