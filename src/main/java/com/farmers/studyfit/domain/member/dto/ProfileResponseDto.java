package com.farmers.studyfit.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {
    private Long id;
    private String loginId;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private String phoneNumber;
    private String profileImg;
    private String role; // TEACHER or STUDENT
    private String school; // 학생인 경우만
    private Integer grade; // 학생인 경우만
}
