package com.farmers.studyfit.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequestDto {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
