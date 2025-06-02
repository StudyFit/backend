package com.farmers.studyfit.domain.connection.dto;

import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long studentId;
    private String studentName;
    private Integer grade;
    private String subject;
    private String themeColor;
    private String note;
    private String address;
    private String friendStatus;
    private Long connectId;
}