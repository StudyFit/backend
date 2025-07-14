package com.farmers.studyfit.domain.calendar.service;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
import com.farmers.studyfit.domain.calendar.repository.CalendarRepository;
import com.farmers.studyfit.domain.common.converter.DtoConverter;
import com.farmers.studyfit.domain.common.dto.ScheduleResponseDto;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodayService {
    private final MemberService memberService;
    private final CalendarRepository calendarRepository;
    private final DtoConverter dtoConverter;
    public List<ScheduleResponseDto> getStudentTodayClass(LocalDate date) {
        Student student = memberService.getCurrentStudentMember();
        List<Calendar> calendarList = calendarRepository.findByDateAndStudentIdAndScheduleType(date, student.getId(), ScheduleType.CLASS);
        List<ScheduleResponseDto> scheduleResponseDtoList = calendarList.stream().map(calendar -> dtoConverter.toScheduleResponse(calendar)).toList();
        return scheduleResponseDtoList;

    }
    public List<ScheduleResponseDto> getTeacherTodayClass(LocalDate date) {
        Teacher teacher = memberService.getCurrentTeacherMember();
        List<Calendar> calendarList = calendarRepository.findByDateAndTeacherIdAndScheduleType(date, teacher.getId(), ScheduleType.CLASS);
        List<ScheduleResponseDto> scheduleResponseDtoList = calendarList.stream().map(calendar -> dtoConverter.toScheduleResponse(calendar)).toList();
        return scheduleResponseDtoList;
    }
}
