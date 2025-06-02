package com.farmers.studyfit.domain.connection.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetColorRequestDto {
    private Long studentId;
    private Long teacherId;
    private String themeColor;
}