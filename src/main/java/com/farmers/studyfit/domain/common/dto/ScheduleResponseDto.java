package com.farmers.studyfit.domain.common.dto;

import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
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
public class ScheduleResponseDto {

    private Long connectionId;
    private Long calendarId;
    private LocalDate date;
    private String teacherName;
    private String teacherProfileImg;
    private String studentName;
    private String studentProfileImg;
    private String grade;
    private String subject;
    private String teacherThemeColor;
    private String studentThemeColor;
    private String address;
    private LocalTime classStartedAt;  // HH:mm 형식 문자열
    private LocalTime classEndedAt;    // HH:mm 형식 문자열
    private String content;
    private ScheduleType type;

}
