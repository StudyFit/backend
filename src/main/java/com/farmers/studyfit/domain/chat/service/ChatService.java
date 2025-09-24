package com.farmers.studyfit.domain.chat.service;

import com.farmers.studyfit.domain.S3Service;
import com.farmers.studyfit.domain.chat.dto.*;
import com.farmers.studyfit.domain.chat.entity.ChatMessage;
import com.farmers.studyfit.domain.chat.entity.ChatRoom;
import com.farmers.studyfit.domain.chat.event.ChatMessageEvent;
import com.farmers.studyfit.domain.chat.repository.ChatMessageRepository;
import com.farmers.studyfit.domain.chat.repository.ChatRoomRepository;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ConnectionRepository connectionRepository;
    private final TeacherRepository teacherRepository;
    private final MemberService memberService;
    private final S3Service s3Service;

    @Transactional
    public Long createChatRoom(CreateChatRoomRequestDto requestDto) {
        Connection connection = connectionRepository.findById(requestDto.getConnectionId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        
        if (chatRoomRepository.existsByConnectionId(requestDto.getConnectionId())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }
        
        ChatRoom chatRoom = ChatRoom.builder()
                .connection(connection)
                .teacher(connection.getTeacher())
                .student(connection.getStudent())
                .build();
        
        return chatRoomRepository.save(chatRoom).getId();
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getChatRoomList() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        List<ChatRoom> chatRooms;
        
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            Teacher teacher = memberService.getCurrentTeacherMember();
            chatRooms = chatRoomRepository.findByTeacherId(teacher.getId());
        } else {
            Student student = memberService.getCurrentStudentMember();
            chatRooms = chatRoomRepository.findByStudentId(student.getId());
        }
        
        return chatRooms.stream()
                .map(this::convertToChatRoomResponseDto)
                .toList();
    }

    @Transactional
    public void sendMessage(SendMessageRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(requestDto.getChatRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        
        String sender = getCurrentUserType();

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(requestDto.getContent())
                .type(ChatMessage.MessageType.valueOf(requestDto.getMessageType()))
                .status(false)
                .build();
        
        chatMessageRepository.save(message);
    }

    @Transactional
    public List<ChatMessageResponseDto> getChatMessages(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        
        validateChatRoomAccess(chatRoom);
        markMessagesAsRead(chatRoom);
        
        List<ChatMessage> messages = chatMessageRepository
                .findByChatRoomOrderByTimeDesc(chatRoom);
        
        return messages.stream()
                .map(this::convertToChatMessageResponseDto)
                .toList();
    }

    public String uploadImageFromBase64(String base64Data) throws IOException {
        try {
            // Base64 데이터에서 헤더 제거 (data:image/jpeg;base64, 부분)
            String base64Image = base64Data;
            if (base64Data.contains(",")) {
                base64Image = base64Data.split(",")[1];
            }
            
            // Base64 디코딩
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            
            // 파일명 생성
            String fileName = "chat/" + UUID.randomUUID().toString() + ".jpg";
            
            // S3에 업로드 (임시 파일로 생성)
            java.io.File tempFile = java.io.File.createTempFile("chat_image", ".jpg");
            java.nio.file.Files.write(tempFile.toPath(), imageBytes);
            
            // S3 업로드
            String uploadedFileName = s3Service.uploadFileFromPath(tempFile.getAbsolutePath(), fileName);
            String imageUrl = s3Service.getFileUrl(uploadedFileName);
            
            // 임시 파일 삭제
            tempFile.delete();
            
            return imageUrl;
            
        } catch (Exception e) {
            log.error("Base64 이미지 업로드 실패: {}", e.getMessage());
            throw new IOException("이미지 업로드에 실패했습니다.", e);
        }
    }
    
    /**
     * WebSocket 메시지 이벤트 처리
     */
    @EventListener
    @Transactional
    public void handleChatMessageEvent(ChatMessageEvent event) {
        try {
            // 이미지 메시지인 경우 S3 업로드 처리
            String content = event.getContent();
            if ("IMAGE".equals(event.getMessageType())) {
                content = uploadImageFromBase64(event.getContent());
            }
            
            // 메시지 저장
            ChatRoom chatRoom = chatRoomRepository.findById(event.getChatRoomId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
            
            ChatMessage message = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(event.getSender())
                    .content(content)
                    .type(ChatMessage.MessageType.valueOf(event.getMessageType()))
                    .status(false)
                    .build();
            
            chatMessageRepository.save(message);
            
            log.info("WebSocket 메시지 저장 완료: chatRoomId={}, sender={}, type={}", 
                    event.getChatRoomId(), event.getSender(), event.getMessageType());
            
        } catch (Exception e) {
            log.error("WebSocket 메시지 이벤트 처리 실패: {}", e.getMessage());
        }
    }

    //헬퍼 메서드
    private void markMessagesAsRead(ChatRoom chatRoom) {
        String currentUserType = getCurrentUserType();
        
        // 현재 사용자가 보낸 메시지가 아닌 읽지 않은 메시지들을 찾기
        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByChatRoomAndStatusFalse(chatRoom)
                .stream()
                .filter(message -> !message.getSender().equals(currentUserType))
                .toList();
        
        // 읽음 처리
        unreadMessages.forEach(message -> message.setStatus(true));
        chatMessageRepository.saveAll(unreadMessages);
    }
    
    private String getCurrentUserType() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            return "TEACHER";
        } else {
            return "STUDENT";
        }
    }
    
    private void validateChatRoomAccess(ChatRoom chatRoom) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        boolean hasAccess = false;
        
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            Teacher teacher = memberService.getCurrentTeacherMember();
            hasAccess = chatRoom.getTeacher().getId().equals(teacher.getId());
        } else {
            Student student = memberService.getCurrentStudentMember();
            hasAccess = chatRoom.getStudent().getId().equals(student.getId());
        }
        
        if (!hasAccess) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }
    
    private ChatRoomResponseDto convertToChatRoomResponseDto(ChatRoom chatRoom) {
        long unreadCount = chatMessageRepository.countByChatRoomAndStatusFalse(chatRoom);
        
        // 마지막 메시지 조회
        var lastMessage = chatMessageRepository.findLastMessageByChatRoom(chatRoom);
        
        // 현재 사용자에 따라 상대방 프로필 이미지 결정
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        String opponentProfileImg;
        
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            // 현재 사용자가 선생님인 경우, 상대방은 학생
            opponentProfileImg = chatRoom.getStudent().getProfileImg();
        } else {
            // 현재 사용자가 학생인 경우, 상대방은 선생님
            opponentProfileImg = chatRoom.getTeacher().getProfileImg();
        }
        
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .connectionId(chatRoom.getConnection().getId())
                .teacherName(chatRoom.getTeacher().getName())
                .studentName(chatRoom.getStudent().getName())
                .opponentProfileImg(opponentProfileImg)
                .unreadCount(unreadCount)
                .lastMessageContent(lastMessage.map(ChatMessage::getContent).orElse(null))
                .lastMessageTime(lastMessage.map(ChatMessage::getTime).orElse(null))
                .lastMessageSender(lastMessage.map(ChatMessage::getSender).orElse(null))
                .build();
    }
    
    private ChatMessageResponseDto convertToChatMessageResponseDto(ChatMessage message) {
        // 발신자 이름 조회 (ERD에 맞춤)
        String senderName = "알 수 없음";
        Long senderId = 0L;
        
        try {
            if ("TEACHER".equals(message.getSender())) {
                // 채팅방의 선생님 정보 사용
                senderName = message.getChatRoom().getTeacher().getName();
                senderId = message.getChatRoom().getTeacher().getId();
            } else if ("STUDENT".equals(message.getSender())) {
                // 채팅방의 학생 정보 사용
                senderName = message.getChatRoom().getStudent().getName();
                senderId = message.getChatRoom().getStudent().getId();
            }
        } catch (Exception e) {
            senderName = "알 수 없음";
            senderId = 0L;
        }

        return ChatMessageResponseDto.builder()
                .id(message.getId())
                .senderId(senderId)
                .senderName(senderName)
                .content(message.getContent())
                .messageType(message.getType().name())
                .sentAt(message.getTime())
                .isRead(message.isStatus())
                .build();
    }
}
