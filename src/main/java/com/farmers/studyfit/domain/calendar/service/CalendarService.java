package com.farmers.studyfit.domain.calendar.service;

import com.farmers.studyfit.domain.calendar.dto.ScheduleRequestDto;
import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
import com.farmers.studyfit.domain.calendar.repository.CalendarRepository;
import com.farmers.studyfit.domain.common.converter.DtoConverter;
import com.farmers.studyfit.domain.common.dto.ScheduleResponseDto;
import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.common.dto.HomeworkResponseDto;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CalendarService {
    private final MemberService memberService;
    private final CalendarRepository calendarRepository;
    private final HomeworkDateRepository homeworkDateRepository;
    private final DtoConverter dtoConverter;
    private final ConnectionRepository connectionRepository;

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


    public List<HomeworkDateResponseDto> getTeacherCalendarHomeworks(LocalDate startDate, LocalDate endDate) {
        Teacher teacher = memberService.getCurrentTeacherMember();
        List<HomeworkDate> homeworkDateList = homeworkDateRepository.findByDateBetweenAndTeacherId(startDate, endDate, teacher.getId());
        return homeworkDateList.stream().map(homeworkDate -> dtoConverter.toHomeworkDateResponse(homeworkDate)).toList();
    }
    public List<HomeworkDateResponseDto> getStudentCalendarHomeworks(LocalDate startDate, LocalDate endDate) {
        Student student = memberService.getCurrentStudentMember();
        List<HomeworkDate> homeworkDateList = homeworkDateRepository.findByDateBetweenAndStudentId(startDate, endDate, student.getId());
        return homeworkDateList.stream().map(homeworkDate -> dtoConverter.toHomeworkDateResponse(homeworkDate)).toList();
    }

    public Long postSchedule(ScheduleRequestDto input){
        Connection c = connectionRepository.findById(input.getConnectionId()).orElseThrow(()->new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        Calendar calendar = Calendar.builder()
                .connection(c)
                .teacher(c.getTeacher())
                .student(c.getStudent())
                .date(input.getDate())
                .startTime(input.getStartTime())
                .endTime(input.getEndTime())
                .content(input.getContent())
                .scheduleType(input.getScheduleType()).build();
        calendarRepository.save(calendar);
        return calendar.getId();
    }
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

    @Transactional
    public void patchSchedule(Long calendarId, ScheduleRequestDto dto) {
        Calendar calendar = calendarRepository.findById(calendarId).orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_NOT_FOUND));

        if(dto.getConnectionId()!=null){
            calendar.setConnection(connectionRepository.findById(dto.getConnectionId()).orElseThrow(()-> new CustomException(ErrorCode.CONNECTION_NOT_FOUND)));
        }
        if(dto.getDate()!=null){
            calendar.setDate(dto.getDate());
        }
        if(dto.getStartTime()!=null){
            calendar.setStartTime(dto.getStartTime());
        }
        if(dto.getEndTime()!=null){
            calendar.setEndTime(dto.getEndTime());
        }
        if(dto.getContent()!=null){
            calendar.setContent(dto.getContent());
        }
        if(dto.getScheduleType()!=null){
            calendar.setScheduleType(dto.getScheduleType());
        }
    }

    @Transactional
    public void deleteSchedule(Long calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId).orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_NOT_FOUND));
        calendarRepository.delete(calendar);
    }
}
