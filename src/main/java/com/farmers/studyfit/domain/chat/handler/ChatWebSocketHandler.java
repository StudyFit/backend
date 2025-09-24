package com.farmers.studyfit.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmers.studyfit.domain.chat.dto.WebSocketMessageDto;
import com.farmers.studyfit.domain.chat.event.ChatMessageEvent;
import com.farmers.studyfit.config.jwt.TokenProvider;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    
    // 채팅방별 세션 관리 (채팅방 ID -> 세션 목록)
    private final Map<Long, Map<String, WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결 시도: {}", session.getId());
        
        try {
            // URL에서 사용자 정보 추출 (예: /ws/chat?token=xxx&chatRoomId=1)
            String query = session.getUri().getQuery();
            if (query != null) {
                Map<String, String> params = parseQuery(query);
                String token = params.get("token");
                String chatRoomId = params.get("chatRoomId");
                
                if (token == null || chatRoomId == null) {
                    log.error("WebSocket 연결 실패: 토큰 또는 채팅방 ID가 누락됨");
                    session.close(CloseStatus.BAD_DATA.withReason("토큰 또는 채팅방 ID가 필요합니다."));
                    return;
                }
                
                // JWT 토큰 검증
                try {
                    tokenProvider.validateToken(token);
                    String userId = tokenProvider.parseClaims(token).getSubject();
                    
                    // 채팅방별 세션 관리
                    chatRoomSessions.computeIfAbsent(Long.parseLong(chatRoomId), k -> new ConcurrentHashMap<>())
                            .put(userId, session);
                    
                    // 세션에 사용자 정보 저장
                    session.getAttributes().put("userId", userId);
                    session.getAttributes().put("chatRoomId", chatRoomId);
                    session.getAttributes().put("token", token);
                    
                    log.info("사용자 {}가 채팅방 {}에 연결됨", userId, chatRoomId);
                    
                } catch (CustomException e) {
                    log.error("WebSocket 연결 실패: 토큰 검증 실패 - {}", e.getMessage());
                    session.close(CloseStatus.POLICY_VIOLATION.withReason("인증에 실패했습니다."));
                    return;
                }
            } else {
                log.error("WebSocket 연결 실패: 쿼리 파라미터가 없음");
                session.close(CloseStatus.BAD_DATA.withReason("필수 파라미터가 누락되었습니다."));
                return;
            }
        } catch (Exception e) {
            log.error("WebSocket 연결 중 오류 발생: {}", e.getMessage());
            session.close(CloseStatus.SERVER_ERROR.withReason("서버 오류가 발생했습니다."));
        }
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                WebSocketMessageDto messageDto = objectMapper.readValue(
                        textMessage.getPayload(), WebSocketMessageDto.class);
                
                // 메시지 처리
                handleChatMessage(session, messageDto);
                
            } catch (Exception e) {
                log.error("메시지 처리 중 오류 발생: {}", e.getMessage());
                sendErrorMessage(session, "메시지 처리 중 오류가 발생했습니다.");
            }
        }
    }
    
    private void handleChatMessage(WebSocketSession session, WebSocketMessageDto messageDto) {
        try {
            String sender = getCurrentUserType(session);
            messageDto.setSender(sender);

            ChatMessageEvent event = new ChatMessageEvent(
                messageDto.getChatRoomId(),
                sender,
                messageDto.getContent(),
                messageDto.getMessageType(),
                session.getId()
            );
            
            eventPublisher.publishEvent(event);
            broadcastToChatRoom(messageDto.getChatRoomId(), messageDto, session.getId());
            
        } catch (Exception e) {
            log.error("메시지 전송 실패: {}", e.getMessage());
            sendErrorMessage(session, "메시지 전송에 실패했습니다.");
        }
    }
    
    private String getCurrentUserType(WebSocketSession session) {
        try {
            String token = (String) session.getAttributes().get("token");
            if (token != null) {
                var claims = tokenProvider.parseClaims(token);
                return (String) claims.get("role"); // ROLE_STUDENT 또는 ROLE_TEACHER
            }
        } catch (Exception e) {
            log.error("사용자 타입 추출 실패: {}", e.getMessage());
        }
        return "ROLE_STUDENT"; // 기본값
    }
    
    private void broadcastToChatRoom(Long chatRoomId, WebSocketMessageDto message, String senderSessionId) {
        Map<String, WebSocketSession> roomSessions = chatRoomSessions.get(chatRoomId);
        if (roomSessions != null) {
            roomSessions.values().stream()
                    .filter(session -> !session.getId().equals(senderSessionId)) // 발신자 제외
                    .filter(WebSocketSession::isOpen)
                    .forEach(session -> {
                        try {
                            String jsonMessage = objectMapper.writeValueAsString(message);
                            session.sendMessage(new TextMessage(jsonMessage));
                        } catch (IOException e) {
                            log.error("메시지 브로드캐스트 실패: {}", e.getMessage());
                        }
                    });
        }
    }
    
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            WebSocketMessageDto errorDto = WebSocketMessageDto.builder()
                    .type("ERROR")
                    .content(errorMessage)
                    .build();
            
            String jsonError = objectMapper.writeValueAsString(errorDto);
            session.sendMessage(new TextMessage(jsonError));
        } catch (IOException e) {
            log.error("에러 메시지 전송 실패: {}", e.getMessage());
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 전송 오류: {}", exception.getMessage());
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket 연결 종료: {}", session.getId());
        
        // 세션 정리
        chatRoomSessions.values().forEach(roomSessions -> 
                roomSessions.values().removeIf(s -> s.getId().equals(session.getId())));
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new ConcurrentHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
