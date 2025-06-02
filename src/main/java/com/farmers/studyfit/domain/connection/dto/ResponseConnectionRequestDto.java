package com.farmers.studyfit.domain.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseConnectionRequestDto {
    private Long studentId;
    private Long teacherId;
    private boolean accepted;
}
