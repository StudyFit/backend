package com.farmers.studyfit.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRoomRequestDto {
    @NotNull(message = "연결 ID는 필수입니다.")
    private Long connectionId;
}
