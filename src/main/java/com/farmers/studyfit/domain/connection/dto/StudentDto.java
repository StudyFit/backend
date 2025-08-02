package com.farmers.studyfit.domain.connection.dto;

import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long connectionId;
    private Long studentId;
    private String studentName;
    private String studentInfo;
    private String subject;
    private String themeColor;
    private String memo;
    private String address;
    private String friendStatus;
}