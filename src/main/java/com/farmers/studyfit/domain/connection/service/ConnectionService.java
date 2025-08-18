package com.farmers.studyfit.domain.connection.service;

import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
import com.farmers.studyfit.domain.calendar.repository.CalendarRepository;
import com.farmers.studyfit.domain.connection.dto.*;
import com.farmers.studyfit.domain.connection.entity.ClassTime;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.entity.ConnectionState;
import com.farmers.studyfit.domain.connection.repository.ClassTimeRepository;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private final StudentRepository studentRepository;
    private final ConnectionRepository connectionRepository;
    private final ClassTimeRepository classTimeRepository;
    private final CalendarRepository calendarRepository;
    private final MemberService memberService;

    public SearchStudentResponseDto findStudentByLoginId(String loginId) {
        Student student = studentRepository.findByLoginId(loginId).orElseThrow();
        return SearchStudentResponseDto.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .studentInfo(student.getSchool()+student.getGrade()).build();

    }

    public void requestConnection(RequestConnectionRequestDto requestConnectionRequestDto) {
        Student student = studentRepository.findById(requestConnectionRequestDto.getStudentId()).orElseThrow();
        Connection connection = Connection.builder()
                .teacher(memberService.getCurrentTeacherMember())
                .student(student)
                .subject(requestConnectionRequestDto.getSubject())
                .studentColor(requestConnectionRequestDto.getThemeColor())
                .address(requestConnectionRequestDto.getAddress())
                .memo(requestConnectionRequestDto.getMemo())
                .status(ConnectionState.REQUESTED)
                .startDate(requestConnectionRequestDto.getStartDate())
                .endDate(requestConnectionRequestDto.getEndDate()).build();
        Connection savedConnection = connectionRepository.save(connection);
        for (ClassTimeDto t : requestConnectionRequestDto.getClassTimeDtoList()) {
            ClassTime classTime = ClassTime.builder()
                    .connection(savedConnection)
                    .day(t.getDay())
                    .startTime(t.getStart())
                    .endTime(t.getEnd()).build();
            classTimeRepository.save(classTime);
        }
    }

    public void acceptConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        connection.setStatus(ConnectionState.ACCEPTED);
        connectionRepository.save(connection);
        LocalDate startDate = connection.getStartDate();
        LocalDate endDate = connection.getEndDate();
        List<ClassTime> classTimeList = classTimeRepository.findByConnectionId(connectionId);
        for (ClassTime c : classTimeList) {
            DayOfWeek targetDay = c.getDay();
            LocalTime startTime = c.getStartTime();
            LocalTime endTime = c.getEndTime();
            LocalDate firstMatchingDate = startDate.with(TemporalAdjusters.nextOrSame(targetDay));

            while (!firstMatchingDate.isAfter(endDate)) {
                Calendar calendar = Calendar.builder()
                        .connection(connection)
                        .teacher(connection.getTeacher())
                        .student(connection.getStudent())
                        .date(firstMatchingDate)
                        .startTime(startTime)
                        .endTime(endTime)
                        .scheduleType(ScheduleType.CLASS).build();
                calendarRepository.save(calendar);
                firstMatchingDate = firstMatchingDate.plusWeeks(1);
            }
        }


    }

    public void rejectConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
            .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
    connection.setStatus(ConnectionState.REJECTED);
    connectionRepository.save(connection);
    }

    public void setTeacherColor(Long connectionId, String themeColor) {
        Connection connection = connectionRepository
                .findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        connection.setTeacherColor(themeColor);
        connectionRepository.save(connection);
    }

    public List<StudentDto> getAllConnectedStudentsByTeacher() {
        Teacher teacher = memberService.getCurrentTeacherMember();
        List<Connection> connections = connectionRepository.findByTeacherId(teacher.getId());

        return connections.stream().map(connection -> {
            Student student = connection.getStudent();
            return StudentDto.builder()
                    .connectionId(connection.getId())
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .studentInfo(student.getSchool()+student.getGrade())
                    .subject(connection.getSubject())
                    .themeColor(connection.getStudentColor())
                    .memo(connection.getMemo())
                    .address(connection.getAddress())
                    .friendStatus(connection.getStatus().name())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<TeacherDto> getAllConnectedTeachersByStudent() {
        Student student = memberService.getCurrentStudentMember();
        List<Connection> connections = connectionRepository.findByStudentId(student.getId());

        return connections.stream().map(connection -> {
            Teacher teacher = connection.getTeacher();
            return TeacherDto.builder()
                    .connectionId(connection.getId())
                    .teacherId(teacher.getId())
                    .teacherName(teacher.getName())
                    .subject(connection.getSubject())
                    .themeColor(connection.getTeacherColor())
                    .connectionStatus(connection.getStatus().name())
                    .build();
        }).collect(Collectors.toList());
    }
}
