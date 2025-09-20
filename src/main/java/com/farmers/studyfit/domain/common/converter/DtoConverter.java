package com.farmers.studyfit.domain.common.converter;

import com.farmers.studyfit.domain.S3Service;
import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.common.dto.HomeworkDateResponseDto;
import com.farmers.studyfit.domain.common.dto.HomeworkResponseDto;
import com.farmers.studyfit.domain.common.dto.ScheduleResponseDto;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.homework.entity.Homework;
import com.farmers.studyfit.domain.homework.entity.HomeworkDate;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DtoConverter {
    private final S3Service s3Service;

    public ScheduleResponseDto toScheduleResponse(Calendar c){
        Teacher t = c.getTeacher();
        Student s = c.getStudent();
        Connection co = c.getConnection();
        return ScheduleResponseDto.builder()
                .connectionId(co.getId())
                .calendarId(c.getId())
                .date(c.getDate())
                .teacherName(t.getName())
                .teacherProfileImg(s3Service.getFileUrl(t.getProfileImg()))
                .teacherThemeColor(co.getTeacherColor())
                .studentName(s.getName())
                .studentProfileImg(s3Service.getFileUrl(s.getProfileImg()))
                .studentThemeColor(co.getStudentColor())
                .grade(s.getSchool()+" "+s.getGrade())
                .subject(co.getSubject())
                .teacherThemeColor(co.getTeacherColor())
                .studentThemeColor(co.getStudentColor())
                .address(co.getAddress())
                .classStartedAt(c.getStartTime())
                .classEndedAt(c.getEndTime())
                .content(c.getContent())
                .type(c.getScheduleType()).build();
    }
    public HomeworkDateResponseDto toHomeworkDateResponse(HomeworkDate h){
        Teacher t = h.getTeacher();
        Student s = h.getStudent();
        Connection c = h.getConnection();
        List<Homework> homeworkList = h.getHomeworkList();
        List<HomeworkResponseDto> homeworkResponseDtoList = homeworkList.stream()
                .map(this::homeworkToHomeworkResponse)
                .toList();

        boolean isAllChecked = homeworkList.stream()
                .allMatch(Homework::isChecked);
        return HomeworkDateResponseDto.builder()
                .connectionId(c.getId())
                .homeworkDateId(h.getId())
                .date(h.getDate())
                .teacherName(t.getName())
                .teacherProfileImg(s3Service.getFileUrl(t.getProfileImg()))
                .studentName(s.getName())
                .studentProfileImg(s3Service.getFileUrl(s.getProfileImg()))
                .grade(s.getSchool()+" "+s.getGrade())
                .subject(c.getSubject())
                .isAllCompleted(isAllChecked)
                //.feedback(h.getFeedback())
                .homeworkList(homeworkResponseDtoList)
                .build();
    }
    public HomeworkResponseDto homeworkToHomeworkResponse(Homework h){
        Boolean isCompleted = h.isChecked();
        Boolean isPhotoRequired = h.isPhotoRequired();
        Boolean isPhotoUploaded;
        if(isPhotoRequired){
            if (isCompleted){
                isPhotoUploaded = true;
            }else{
                isPhotoUploaded = false;
            }
        } else {
            isPhotoUploaded = null;
        }
        return HomeworkResponseDto.builder()
                .homeworkId(h.getId())
                .content(h.getContent())
                .isCompleted(h.isChecked())
                .isPhotoRequired(h.isPhotoRequired())
                .isPhotoUploaded(isPhotoUploaded).build();

    }
}
