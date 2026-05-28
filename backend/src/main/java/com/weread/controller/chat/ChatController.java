package com.weread.controller.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weread.common.ApiResponse;
import com.weread.dto.chat.SendMessageRequest;
import com.weread.service.chat.ChatService;
import com.weread.vo.chat.SendMessageResponseVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<SendMessageResponseVO>> sendMessage(@RequestBody SendMessageRequest request) {
        Integer currentUserId = currentUserId();
        SendMessageResponseVO data = chatService.sendMessage(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(data));
    }

    @GetMapping("/message")
    public ApiResponse<?> getMessages(@RequestParam Long conversationId) {
        return ApiResponse.ok(chatService.getMessages(conversationId));
    }

    @PostMapping("/message/{messageId}/withdraw")
    public ApiResponse<?> recallMessage(@PathVariable Long messageId) {
        chatService.recallMessage(messageId, currentUserId());
        return ApiResponse.ok();
    }

    @GetMapping("/conversations")
    public ApiResponse<?> getConversations() {
        return ApiResponse.ok(chatService.getConversations(currentUserId()));
    }

    @GetMapping("/conversation/{userId}")
    public ApiResponse<?> getConversationMessages(
            @PathVariable Integer userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") Integer limit) {
        Long cursorId = parseCursor(cursor);
        return ApiResponse.ok(chatService.getConversationMessages(currentUserId(), userId, cursorId, limit));
    }

    private Long parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(cursor.trim());
        } catch (NumberFormatException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "cursor 格式无效");
        }
    }

    private Integer currentUserId() {
        return (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
