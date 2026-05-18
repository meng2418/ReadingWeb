package com.weread.controller.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import com.weread.common.ApiResponse;
import com.weread.dto.chat.SendMessageRequest;
import com.weread.entity.chat.ChatConversationEntity;
import com.weread.repository.chat.ChatConversationRepository;
import com.weread.service.chat.ChatService;
import com.weread.vo.chat.MessageVO;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

        private final ChatService chatService;

        private final ChatConversationRepository chatConversationRepository;

        @PostMapping("/message")
        public ApiResponse<?> sendMessage(
                        @org.springframework.web.bind.annotation.RequestBody SendMessageRequest request) {

                Integer currentUserId = (Integer) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                return ApiResponse.ok(
                                chatService.sendMessage(currentUserId, request));
        }

        @GetMapping("/message")
        public ApiResponse<?> getMessages(
                        @RequestParam Long conversationId) {

                return ApiResponse.ok(
                                chatService.getMessages(conversationId));
        }

        // 撤回消息接口
        @PostMapping("/message/{messageId}/withdraw")
        public ApiResponse<?> recallMessage(
                        @PathVariable Long messageId) {
                System.out.println("进入 recallMessage");

                Integer currentUserId = (Integer) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                chatService.recallMessage(messageId, currentUserId);

                return ApiResponse.ok();
        }

        @GetMapping("/conversations")
        public ApiResponse<?> getConversations() {
                Integer currentUserId = (Integer) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();
                return ApiResponse.ok(chatService.getConversations(currentUserId));
        }

        // 获取和指定用户的聊天记录
        @GetMapping("/conversation/{userId}")
        public ApiResponse<List<MessageVO>> getConversationMessages(@PathVariable Integer userId) {
                Integer currentUserId = (Integer) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();
                Integer targetUserId = Integer.valueOf(userId);

                // 双向查询：我-对方 或 对方-我
                ChatConversationEntity conv = chatConversationRepository
                                .findByUser1IdAndUser2Id(currentUserId, targetUserId)
                                .orElseGet(() -> chatConversationRepository
                                                .findByUser1IdAndUser2Id(targetUserId, currentUserId)
                                                .orElse(null));

                if (conv == null) {
                        return ApiResponse.ok(new ArrayList<>());
                }
                List<MessageVO> list = chatService.getMessages(conv.getConversationId());
                return ApiResponse.ok(list);
        }
}