package com.farmers.studyfit.domain.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDto {
    private Long connectionId;
    private Long teacherId;
    private String teacherName;
    private String subject;
    private String themeColor;
    private String connectionStatus;
}