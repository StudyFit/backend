package com.farmers.studyfit.domain.chat.service;

import com.farmers.studyfit.domain.S3Service;
import com.farmers.studyfit.domain.chat.dto.*;
import com.farmers.studyfit.domain.chat.entity.ChatMessage;
import com.farmers.studyfit.domain.chat.entity.ChatRoom;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ConnectionRepository connectionRepository;
    private final TeacherRepository teacherRepository;
    private final MemberService memberService;
    private final S3Service s3Service;

    /**
     * 1. 채팅방 생성하기
     */
    @Transactional
    public Long createChatRoom(CreateChatRoomRequestDto requestDto) {
        Connection connection = connectionRepository.findById(requestDto.getConnectionId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        
        // 이미 채팅방이 존재하는지 확인
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

    /**
     * 2. 채팅방 목록 조회하기
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getChatRoomList() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        List<ChatRoom> chatRooms;
        
        // 선생님인 경우
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            Teacher teacher = memberService.getCurrentTeacherMember();
            chatRooms = chatRoomRepository.findByTeacherId(teacher.getId());
        } else {
            // 학생인 경우
            Student student = memberService.getCurrentStudentMember();
            chatRooms = chatRoomRepository.findByStudentId(student.getId());
        }
        
        return chatRooms.stream()
                .map(this::convertToChatRoomResponseDto)
                .toList();
    }

    /**
     * 3. 채팅 메시지 저장하기 (텍스트)
     */
    @Transactional
    public void sendMessage(SendMessageRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(requestDto.getChatRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        
        // 발신자 정보 설정 (ERD에 맞춤)
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

    /**
     * 4. 과거 메시지 조회하기 (전체) + 읽음 처리
     */
    @Transactional
    public List<ChatMessageResponseDto> getChatMessages(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        
        // 권한 검증
        validateChatRoomAccess(chatRoom);
        
        // 읽지 않은 메시지들을 읽음 처리
        markMessagesAsRead(chatRoom);
        
        List<ChatMessage> messages = chatMessageRepository
                .findByChatRoomOrderByTimeDesc(chatRoom);
        
        return messages.stream()
                .map(this::convertToChatMessageResponseDto)
                .toList();
    }


    /**
     * 6. 이미지 파일 전송하기
     */
    @Transactional
    public void sendImage(Long chatRoomId, MultipartFile imageFile) throws IOException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        
        // S3에 이미지 업로드
        String fileName = s3Service.uploadFile(imageFile);
        String imageUrl = s3Service.getFileUrl(fileName);
        
        // 발신자 정보 설정 (ERD에 맞춤)
        String sender = getCurrentUserType();

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(imageUrl)
                .type(ChatMessage.MessageType.IMAGE)
                .status(false)
                .build();
        
        chatMessageRepository.save(message);
    }

    // ========== 헬퍼 메서드들 ==========
    
    /**
     * 채팅방에 들어갔을 때 읽지 않은 메시지들을 읽음 처리
     */
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
    
    /**
     * 현재 사용자 타입 반환 (TEACHER 또는 STUDENT)
     */
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
        
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .connectionId(chatRoom.getConnection().getId())
                .teacherName(chatRoom.getTeacher().getName())
                .studentName(chatRoom.getStudent().getName())
                .unreadCount(unreadCount)
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
