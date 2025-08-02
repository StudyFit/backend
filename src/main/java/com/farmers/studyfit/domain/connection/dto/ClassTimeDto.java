package com.farmers.studyfit.domain.connection.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassTimeDto {
    private DayOfWeek day;
    private LocalTime start;
    private LocalTime end;
}
