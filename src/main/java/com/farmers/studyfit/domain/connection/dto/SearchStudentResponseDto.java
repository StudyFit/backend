package com.farmers.studyfit.domain.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchStudentResponseDto {
    private Long studentId;
    private String studentName;
    private String studentInfo;
}
