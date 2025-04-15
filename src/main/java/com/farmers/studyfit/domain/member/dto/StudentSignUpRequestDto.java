package com.farmers.studyfit.domain.member.dto;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSignUpRequestDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private LocalDate birth;

    @NotBlank
    private String school;

    @Min(1)
    private int grade;

    @NotBlank
    private String phoneNum;
}
