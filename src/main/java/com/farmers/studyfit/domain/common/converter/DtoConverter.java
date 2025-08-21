package com.farmers.studyfit.domain.common.converter;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.common.dto.HomeworkResponseDto;
import com.farmers.studyfit.domain.common.dto.ScheduleResponseDto;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DtoConverter {

    public ScheduleResponseDto toScheduleResponse(Calendar c){
        return ScheduleResponseDto.builder()
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
                .type(c.getScheduleType()).build();
    }
    public HomeworkDateResponseDto toHomeworkDateResponse(HomeworkDate h){
        List<Homework> homeworkList = h.getHomeworkList();
        List<HomeworkResponseDto> homeworkResponseDtoList = homeworkList.stream()
                .map(this::homeworkToHomeworkResponse)
                .toList();

        boolean isAllChecked = homeworkList.stream()
                .allMatch(Homework::isChecked);
        return HomeworkDateResponseDto.builder()
                .connectionId(h.getConnection().getId())
                .homeworkDateId(h.getId())
                .date(h.getDate())
                .teacherName(h.getTeacher().getName())
                .studentName(h.getStudent().getName())
                .grade(h.getStudent().getSchool()+h.getStudent().getGrade())
                .subject(h.getConnection().getSubject())
                .isAllChecked(isAllChecked)
                //.feedback(h.getFeedback())
                .homeworkList(homeworkResponseDtoList)
                .build();
    }
    public HomeworkResponseDto homeworkToHomeworkResponse(Homework h){
        return HomeworkResponseDto.builder()
                .homeworkId(h.getId())
                .content(h.getContent())
                .isChecked(h.isChecked()).build();
    }
}
