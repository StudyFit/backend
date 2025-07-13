package com.farmers.studyfit.domain.calendar.service;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.repository.CalendarRepository;
import com.farmers.studyfit.domain.common.converter.DtoConverter;
import com.farmers.studyfit.domain.common.dto.ScheduleResponseDto;
import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.common.dto.HomeworkResponseDto;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CalendarService {
    private final MemberService memberService;
    private final CalendarRepository calendarRepository;
    private final HomeworkDateRepository homeworkDateRepository;
    private final DtoConverter dtoConverter;

    public List<ScheduleResponseDto> getStudentCalendarSchedule(LocalDate startDate, LocalDate endDate) {
        Student student = memberService.getCurrentStudentMember();
        List<Calendar> calendarList = calendarRepository.findByDateBetweenAndStudentId(startDate, endDate, student.getId());
        List<ScheduleResponseDto> scheduleResponseDtoList = calendarList.stream().map(calendar -> dtoConverter.toScheduleResponse(calendar)).toList();
        return scheduleResponseDtoList;

    }
    public List<ScheduleResponseDto> getTeacherCalendarSchedule(LocalDate startDate, LocalDate endDate) {
        Teacher teacher = memberService.getCurrentTeacherMember();
        List<Calendar> calendarList = calendarRepository.findByDateBetweenAndTeacherId(startDate, endDate, teacher.getId());
        List<ScheduleResponseDto> scheduleResponseDtoList = calendarList.stream().map(calendar -> dtoConverter.toScheduleResponse(calendar)).toList();
        return scheduleResponseDtoList;
    }


    public List<HomeworkDateResponseDto> getTeacherCalendarHomework(LocalDate startDate, LocalDate endDate) {
        Teacher teacher = memberService.getCurrentTeacherMember();
        List<HomeworkDate> homeworkDateList = homeworkDateRepository.findByDateBetweenAndTeacherId(startDate, endDate, teacher.getId());
        return homeworkDateList.stream().map(homeworkDate -> dtoConverter.toHomeDateResponse(homeworkDate)).toList();
    }
    public List<HomeworkDateResponseDto> getStudentCalendarHomework(LocalDate startDate, LocalDate endDate) {
        Student student = memberService.getCurrentStudentMember();
        List<HomeworkDate> homeworkDateList = homeworkDateRepository.findByDateBetweenAndStudentId(startDate, endDate, student.getId());
        return homeworkDateList.stream().map(homeworkDate -> dtoConverter.toHomeDateResponse(homeworkDate)).toList();
    }
}
