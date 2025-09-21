package com.farmers.studyfit.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType; // TEXT, IMAGE
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
    private boolean isRead;
}
