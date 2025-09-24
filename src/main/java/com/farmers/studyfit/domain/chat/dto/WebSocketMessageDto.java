package com.farmers.studyfit.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageDto {
    
    private Long chatRoomId;
    private String sender; // TEACHER 또는 STUDENT
    private String senderName;
    private String content;
    private String type; // TEXT, IMAGE, ERROR
    private String messageType; // TEXT, IMAGE
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
    
    public SendMessageRequestDto toSendMessageRequestDto() {
        return new SendMessageRequestDto(chatRoomId, content, messageType != null ? messageType : "TEXT");
    }
}