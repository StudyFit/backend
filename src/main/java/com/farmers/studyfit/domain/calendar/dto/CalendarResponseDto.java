package com.farmers.studyfit.domain.calendar.dto;

import com.farmers.studyfit.domain.connection.dto.ScheduleDto;
import com.farmers.studyfit.domain.homework.dto.HomeworkDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class CalendarResponseDto {
    private List<ScheduleDto> schedules;
    private List<HomeworkDto> homeworks;
}