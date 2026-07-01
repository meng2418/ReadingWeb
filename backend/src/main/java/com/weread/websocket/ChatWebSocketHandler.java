package com.weread.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weread.dto.chat.SendMessageRequest;
import com.weread.service.chat.ChatService;
import com.weread.util.JwtUtil;
import com.weread.vo.chat.MessageVO;
import com.weread.vo.chat.SendMessageResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatService chatService, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String token = parseToken(session);
        if (token == null) {
            closeSession(session, "Missing token");
            return;
        }
        Integer userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            closeSession(session, "Invalid token");
            return;
        }
        userSessions.put(userId, session);
        session.getAttributes().put("userId", userId);
        log.info("WebSocket connected userId={}", userId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        if (userId == null) {
            closeSession(session, "Unauthorized");
            return;
        }

        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String action = String.valueOf(payload.get("action"));
            if ("sendMessage".equals(action)) {
                processSendMessage(userId, payload);
            } else {
                log.warn("Unknown WS action: {}", action);
                sendError(session, "unknown action");
            }
        } catch (Exception e) {
            log.error("WebSocket handle message failed", e);
            sendError(session, "invalid message payload");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.remove(userId, session);
            log.info("WebSocket disconnected userId={}", userId);
        }
    }

    private void processSendMessage(Integer senderId, Map<String, Object> payload) {
        Integer receiverId = payload.containsKey("receiverId") ? ((Number) payload.get("receiverId")).intValue() : null;
        String messageType = String.valueOf(payload.get("messageType"));
        String content = payload.containsKey("content") ? String.valueOf(payload.get("content")) : "";
        Object bookInfo = payload.get("bookInfo");

        if (receiverId == null || receiverId <= 0) {
            log.warn("WS sendMessage missing receiverId");
            return;
        }

        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(receiverId);
        request.setMessageType(messageType);
        request.setContent(content);
        request.setBookInfo(objectMapper.convertValue(bookInfo, SendMessageRequest.BookInfo.class));

        try {
            SendMessageResponseVO response = chatService.sendMessage(senderId, request);
            MessageVO saved = response.getMessage();
            if (saved != null) {
                sendOutboundMessage(saved);
                forwardToReceiver(saved);
            }
        } catch (Exception e) {
            log.error("WS sendMessage failed", e);
            sendError(userSessions.get(senderId), "send message failed");
        }
    }

    private void sendOutboundMessage(MessageVO saved) {
        try {
            Map<String, Object> wrapper = Map.of(
                    "type", "message",
                    "payload", Map.of("message", saved)
            );
            String text = objectMapper.writeValueAsString(wrapper);
            WebSocketSession session = userSessions.get(saved.getSenderId());
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(text));
            }
        } catch (Exception e) {
            log.error("sendOutboundMessage failed", e);
        }
    }

    private void forwardToReceiver(MessageVO saved) {
        try {
            Integer receiverId = saved.getReceiverId();
            WebSocketSession receiverSession = userSessions.get(receiverId);
            if (receiverSession != null && receiverSession.isOpen()) {
                Map<String, Object> wrapper = Map.of(
                        "type", "message",
                        "payload", Map.of("message", saved)
                );
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(wrapper)));
            }
        } catch (Exception e) {
            log.error("forwardToReceiver failed", e);
        }
    }

    private String parseToken(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && "token".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    private void sendError(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("type", "error", "payload", Map.of("message", message)))));
        } catch (Exception e) {
            log.error("sendError failed", e);
        }
    }

    private void closeSession(WebSocketSession session, String reason) {
        sendError(session, reason);
        try {
            session.close(CloseStatus.BAD_DATA);
        } catch (Exception ignore) {
        }
    }
}
