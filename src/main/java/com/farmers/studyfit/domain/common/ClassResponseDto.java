package com.farmers.studyfit.domain.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponseDto {

    private Long connectionId;
    private Long calendarId;
    private LocalDate date;
    private String name;
    private String grade;       // 학생이 조회할 경우 null 가능
    private String subject;
    private String themeColor;
    private String address;
    private LocalTime classStartedAt;  // HH:mm 형식 문자열
    private LocalTime classEndedAt;    // HH:mm 형식 문자열
    private String content;
    private String type;

}
