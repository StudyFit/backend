package com.farmers.studyfit.domain.member.dto;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class TeacherSignUpRequestDto {
    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private LocalDate birth;

    @NotBlank
    private String phoneNum;
}
