package com.farmers.studyfit.domain.calendar.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarItemDto {

    // 공통 필드
    private String date;          // ex: "2025-05-01"
    private String day;           // ex: "MONDAY"
    private String type;          // "SCHEDULE", "HOMEWORK" 등

    // 시간 관련
    private String startTime;     // ex: "14:00"
    private String endTime;       // ex: "15:00"

    // 수업/일정 공통
    private String studentName;
    private String subject;
    private String content;
    private String themeColor;

    // 식별자
    private Long scheduleId;          // 수업일정 ID
    private Long homeworkId;          // 숙제 ID
    private Long homeworkDateGroupId; // 숙제 그룹 ID

    // 숙제 전용
    private Boolean isAssigned;
    private Boolean isImgRequired;
}