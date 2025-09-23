package com.farmers.studyfit.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private Long id;
    private Long connectionId;
    private String teacherName;
    private String studentName;
    private long unreadCount; // 읽지 않은 메시지 수
}
