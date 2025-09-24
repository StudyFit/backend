package com.farmers.studyfit.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private Long id;
    private Long connectionId;
    private String teacherName;
    private String studentName;
    private String opponentProfileImg; // 상대방 프로필 이미지
    private long unreadCount; // 읽지 않은 메시지 수
    private String lastMessageContent; // 마지막 메시지 내용
    private LocalDateTime lastMessageTime; // 마지막 메시지 시간
    private String lastMessageSender; // 마지막 메시지 발신자 (TEACHER 또는 STUDENT)
}
