package com.farmers.studyfit.domain.calendar.service;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.repository.CalendarRepository;
import com.farmers.studyfit.domain.common.ClassResponseDto;
import com.farmers.studyfit.domain.common.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.member.entity.Student;
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

    /*public List<ClassResponseDto> getStudentCalendarClass(LocalDate startDate, LocalDate endDate) {
        Student student = memberService.getCurrentStudentMember();
        List<Calendar> calendarList = calendarRepository.findByDateBetween(startDate, endDate);

    }
    public List<ClassResponseDto> getTeacherCalendarClass(LocalDate startDate, LocalDate endDate) {
    }


    public List<HomeworkDateResponseDto> getCalendarHomework(String role, LocalDate startDate, LocalDate endDate) {
    }*/
}
