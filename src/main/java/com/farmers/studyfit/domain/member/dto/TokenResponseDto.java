package com.farmers.studyfit.domain.member.dto;

import lombok.*;

@Getter @AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String role;
}