package com.farmers.studyfit.domain.calendar.dto;

import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
import com.sun.jdi.ClassType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDto {
    private Long connectionId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String content;
    private ScheduleType scheduleType;
}
