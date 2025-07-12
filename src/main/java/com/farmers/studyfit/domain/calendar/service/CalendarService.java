package com.farmers.studyfit.domain.calendar.service;

import com.farmers.studyfit.domain.calendar.dto.CalendarResponseDto;
import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.repository.CalendarRepository;
import com.farmers.studyfit.domain.common.ClassResponseDto;
import com.farmers.studyfit.domain.common.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.common.HomeworkResponseDto;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.homework.repository.HomeworkDateRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CalendarService {
    private final MemberService memberService;
    private final CalendarRepository calendarRepository;
    private final HomeworkDateRepository homeworkDateRepository;

    public List<ClassResponseDto> getStudentCalendarClass(LocalDate startDate, LocalDate endDate) {
        Student student = memberService.getCurrentStudentMember();
        List<Calendar> calendarList = calendarRepository.findByDateBetweenAndStudentId(startDate, endDate, student.getId());
        List<ClassResponseDto> classResponseDtoList = calendarList.stream().map(this::calendarToClassResponse).toList();
        return classResponseDtoList;

    }
    public List<ClassResponseDto> getTeacherCalendarClass(LocalDate startDate, LocalDate endDate) {
        Teacher teacher = memberService.getCurrentTeacherMember();
        List<Calendar> calendarList = calendarRepository.findByDateBetweenAndTeacherId(startDate, endDate, teacher.getId());
        List<ClassResponseDto> classResponseDtoList = calendarList.stream().map(this::calendarToClassResponse).toList();
        return classResponseDtoList;
    }


    public List<HomeworkDateResponseDto> getTeacherCalendarHomework(LocalDate startDate, LocalDate endDate) {
        Teacher teacher = memberService.getCurrentTeacherMember();
        List<HomeworkDate> homeworkDateList = homeworkDateRepository.findByDateBetweenAndTeacherId(startDate, endDate, teacher.getId());
        return homeworkDateList.stream().map(this::homeworkDateToHomeDateResponse).toList();
    }
    public List<HomeworkDateResponseDto> getStudentCalendarHomework(LocalDate startDate, LocalDate endDate) {
        Student student = memberService.getCurrentStudentMember();
        List<HomeworkDate> homeworkDateList = homeworkDateRepository.findByDateBetweenAndStudentId(startDate, endDate, student.getId());
        return homeworkDateList.stream().map(this::homeworkDateToHomeDateResponse).toList();
    }

    private ClassResponseDto calendarToClassResponse(Calendar c){
        return ClassResponseDto.builder()
                .connectionId(c.getConnection().getId())
                .calendarId(c.getId())
                .date(c.getDate())
                .teacherName(c.getTeacher().getName())
                .studentName(c.getStudent().getName())
                .grade(c.getStudent().getSchool()+c.getStudent().getGrade())
                .subject(c.getConnection().getSubject())
                .teacherThemeColor(c.getConnection().getTeacherColor())
                .studentThemeColor(c.getConnection().getStudentColor())
                .address(c.getConnection().getAddress())
                .classStartedAt(c.getStartTime())
                .classEndedAt(c.getEndTime())
                .content(c.getContent())
                .type(c.getCalendarType()).build();
    }
    private HomeworkDateResponseDto homeworkDateToHomeDateResponse(HomeworkDate h){
        List<Homework> homeworkList = h.getHomeworkList();
        List<HomeworkResponseDto> homeworkResponseDtoList = homeworkList.stream()
                .map(this::homeworkToHomeworkResponse)
                .toList();

        boolean isAllCompleted = homeworkList.stream()
                .allMatch(Homework::isCompleted);
        return HomeworkDateResponseDto.builder()
                .connectionId(h.getConnection().getId())
                .homeworkDateId(h.getId())
                .date(h.getDate())
                .teacherName(h.getTeacher().getName())
                .studentName(h.getStudent().getName())
                .grade(h.getStudent().getSchool()+h.getStudent().getGrade())
                .subject(h.getConnection().getSubject())
                .isAllCompleted(isAllCompleted)
                //.feedback(h.getFeedback())
                .homeworkList(homeworkResponseDtoList)
                .build();
    }
    private HomeworkResponseDto homeworkToHomeworkResponse(Homework h){
        return HomeworkResponseDto.builder()
                .homeworkId(h.getId())
                .content(h.getContent())
                .isCompleted(h.isCompleted())
                .isPhotoRequired(h.isPhotoRequired()).build();
    }
}
