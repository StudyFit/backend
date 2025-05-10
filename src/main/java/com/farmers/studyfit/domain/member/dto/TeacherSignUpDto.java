package com.farmers.studyfit.domain.member.dto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSignUpDto {
    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotNull @Past
    private LocalDate birth;

    @NotBlank
    private String phoneNumber;
}
