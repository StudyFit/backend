package com.farmers.studyfit.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;
    
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}