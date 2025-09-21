package com.farmers.studyfit.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDto {
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long chatRoomId;
    
    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String content;
    
    private String messageType = "TEXT"; // TEXT, IMAGE
}
