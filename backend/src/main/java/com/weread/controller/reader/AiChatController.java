package com.weread.controller.reader;

import com.weread.dto.Result;
import com.weread.dto.reader.AiChatSendMessageRequestDTO;
import com.weread.service.reader.AiChatService;
import com.weread.vo.reader.AiChatHistoryVO;
import com.weread.vo.reader.AiChatSendMessageResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
@Tag(name = "AI Chat", description = "Book-centric AI Q&A")
@SecurityRequirement(name = "bearerAuth")
public class AiChatController {

    private final AiChatService aiChatService;

    @GetMapping("/session/{bookId}")
    @Operation(summary = "Get chat history", description = "Get current user's AI chat history for a book")
    public Result<AiChatHistoryVO> getHistory(
            @PathVariable Integer bookId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer cursor,
            @AuthenticationPrincipal Integer userId
    ) {
        Integer uid = (userId != null) ? userId : getCurrentUserId();
        return Result.success(aiChatService.getHistory(uid, bookId, limit, cursor));
    }

    @PostMapping("/session/{bookId}/message")
    @Operation(summary = "Send message", description = "Send a message and get AI response")
    public ResponseEntity<Result<AiChatSendMessageResponseVO>> sendMessage(
            @PathVariable Integer bookId,
            @RequestBody AiChatSendMessageRequestDTO body,
            @AuthenticationPrincipal Integer userId
    ) {
        if (body == null || body.getMessage() == null || body.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(Result.fail("message is required"));
        }
        Integer uid = (userId != null) ? userId : getCurrentUserId();
        AiChatSendMessageResponseVO data = aiChatService.sendMessage(uid, bookId, body.getMessage());

        Result<AiChatSendMessageResponseVO> result = new Result<>();
        result.setCode(201);
        result.setMessage("success");
        result.setData(data);
        return ResponseEntity.status(201).body(result);
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("unauthenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Integer) {
            return (Integer) principal;
        }
        if (principal instanceof com.weread.entity.user.UserEntity user) {
            Object userIdObj = user.getUserId();
            if (userIdObj instanceof Integer) {
                return (Integer) userIdObj;
            }
            if (userIdObj instanceof Long) {
                return ((Long) userIdObj).intValue();
            }
        }
        if (principal instanceof Long) {
            return ((Long) principal).intValue();
        }
        if (principal instanceof String) {
            return Integer.parseInt((String) principal);
        }
        throw new RuntimeException("unknown principal type: " + principal.getClass().getName());
    }
}
