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
import com.weread.vo.book.BookVO;
import com.weread.vo.chat.ConversationVO;
import com.weread.vo.chat.MessageVO;
import com.weread.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

        private final ChatConversationRepository conversationRepository;

        private final ChatMessageRepository messageRepository;

        private final BookRepository bookRepository;

        private final UserRepository userRepository;

        /**
         * 发送消息
         */
        @Override
        public ChatMessageEntity sendMessage(
                        Integer senderId,
                        SendMessageRequest request) {

                // 1. 查找会话（双向）
                ChatConversationEntity conversation = conversationRepository
                                .findByUser1IdAndUser2Id(
                                                senderId,
                                                request.getReceiverId())
                                .orElseGet(() -> conversationRepository
                                                .findByUser1IdAndUser2Id(
                                                                request.getReceiverId(),
                                                                senderId)
                                                .orElse(null));

                // 不存在则创建
                if (conversation == null) {
                        conversation = new ChatConversationEntity();
                        conversation.setUser1Id(senderId);
                        conversation.setUser2Id(request.getReceiverId());
                        conversation = conversationRepository.save(
                                        conversation);
                }

                // 2. 创建消息
                ChatMessageEntity message = new ChatMessageEntity();
                message.setConversation(conversation);
                message.setSenderId(senderId);
                message.setMessageType(request.getMessageType());
                message.setContent(request.getContent());
                message.setIsRecalled(false);

                // 3. 图书消息处理
                if ("book".equals(
                                request.getMessageType())) {

                        if (request.getBookInfo() == null
                                        || request.getBookInfo()
                                                        .getBookId() == null) {

                                throw new RuntimeException(
                                                "bookId不能为空");
                        }

                        BookEntity book = bookRepository.findByBookId(
                                        request.getBookInfo().getBookId()).orElseThrow(
                                                        () -> new RuntimeException("书籍不存在"));

                        message.setBook(book);
                }

                // 4. 保存消息
                ChatMessageEntity savedMessage = messageRepository.save(message);

                // 5. 更新会话信息
                String lastMessage;
                switch (request.getMessageType()) {
                        case "image":
                                lastMessage = "[图片]";
                                break;

                        case "book":
                                lastMessage = "[分享书籍]";
                                break;

                        default:
                                lastMessage = request.getContent();
                }

                conversation.setLastMessageContent(lastMessage);

                conversation.setLastMessageTime(LocalDateTime.now());

                conversationRepository.save(conversation);

                return savedMessage;
        }

        /**
         * 获取聊天记录
         */
        @Override
        public List<MessageVO> getMessages(
                        Long conversationId) {

                ChatConversationEntity conv = new ChatConversationEntity();
                conv.setConversationId(conversationId);

                List<ChatMessageEntity> messages = messageRepository
                                .findByConversationOrderByCreatedAtAsc(conv);

                List<MessageVO> result = new ArrayList<>();
                for (ChatMessageEntity msg : messages) {
                        MessageVO vo = new MessageVO();
                        vo.setMessageId(msg.getMessageId());
                        vo.setConversationId(conversationId);
                        vo.setSenderId(msg.getSenderId());
                        vo.setMessageType(msg.getMessageType());
                        vo.setContent(msg.getIsRecalled() ? "消息已撤回" : msg.getContent());
                        vo.setIsRecalled(msg.getIsRecalled());
                        vo.setCreatedAt(msg.getCreatedAt());

                        // 书籍消息赋值
                        if (msg.getBook() != null) {
                                BookVO bvo = new BookVO();
                                bvo.setBookId(msg.getBook().getBookId());
                                bvo.setBookTitle(msg.getBook().getTitle());
                                bvo.setCover(msg.getBook().getCover());
                                bvo.setAuthorName(msg.getBook().getAuthorName());
                                vo.setBook(bvo);
                        }

                        result.add(vo);
                }
                return result;
        }

        @Override
        public List<ConversationVO> getConversations(Integer userId) {
                // 查询我参与的所有会话
                List<ChatConversationEntity> list = conversationRepository
                                .findByUser1IdOrUser2Id(userId, userId);

                List<ConversationVO> result = new ArrayList<>();

                for (ChatConversationEntity conv : list) {
                        ConversationVO vo = new ConversationVO();
                        vo.setConversationId(conv.getConversationId());
                        vo.setLastMessageContent(conv.getLastMessageContent());
                        vo.setLastMessageTime(conv.getLastMessageTime());

                        // 识别对方ID
                        Integer targetId = userId.equals(conv.getUser1Id()) ? conv.getUser2Id() : conv.getUser1Id();
                        Integer targetUserId = targetId.intValue();
                        // 查询对方信息
                        UserEntity user = userRepository.findById(targetUserId).orElse(null);
                        if (user != null) {
                                vo.setTargetUserId(user.getUserId());
                                vo.setNickname(user.getUsername());
                                vo.setAvatar(user.getAvatar());
                        }

                        result.add(vo);
                }
                return result;
        }

        /**
         * 撤回消息
         */
        @Override
        @Transactional
        public void recallMessage(
                        Long messageId,
                        Integer userId) {
                // 1. 查询消息
                ChatMessageEntity message = messageRepository.findById(messageId)
                                .orElseThrow(() -> new RuntimeException(
                                                "消息不存在"));
                // 2. 只能撤回自己的消息
                if (!message.getSenderId().equals(userId)) {
                        throw new RuntimeException(
                                        "不能撤回别人的消息");
                }
                // 3. 已撤回检查
                if (Boolean.TRUE.equals(
                                message.getIsRecalled())) {

                        throw new RuntimeException(
                                        "消息已经撤回");
                }
                // 4. 撤回时间限制（2分钟）
                LocalDateTime now = LocalDateTime.now();
                if (message.getCreatedAt().plusMinutes(2).isBefore(now)) {
                        throw new RuntimeException("消息超过2分钟无法撤回");
                }
                // 5. 执行撤回
                message.setIsRecalled(true);
                message.setRecalledAt(now);
                messageRepository.save(message);

                // 6. 更新会话最后消息
                ChatConversationEntity conversation = message.getConversation();
                ChatMessageEntity lastMessage = messageRepository
                                .findTopByConversationOrderByCreatedAtDesc(
                                                conversation);
                if (lastMessage != null) {
                        String content;
                        // 最后一条就是当前撤回的消息
                        if (lastMessage.getMessageId().equals(messageId)) {
                                content = "消息已撤回";
                        } else {
                                switch (lastMessage.getMessageType()) {
                                        case "image":
                                                content = "[图片]";
                                                break;
                                        case "book":
                                                content = "[分享书籍]";
                                                break;
                                        default:
                                                content = Boolean.TRUE.equals(
                                                                lastMessage.getIsRecalled())
                                                                                ? "消息已撤回"
                                                                                : lastMessage.getContent();
                                }
                        }

                        conversation.setLastMessageContent(content);
                        conversation.setLastMessageTime(LocalDateTime.now());
                        conversationRepository.save(conversation);
                }
        }
}
