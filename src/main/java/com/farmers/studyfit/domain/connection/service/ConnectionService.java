package com.farmers.studyfit.domain.connection.service;

import com.farmers.studyfit.domain.S3Service;
import com.farmers.studyfit.domain.calendar.entity.Calendar;
import com.farmers.studyfit.domain.calendar.entity.ScheduleType;
import com.farmers.studyfit.domain.calendar.repository.CalendarRepository;
import com.farmers.studyfit.domain.common.converter.DateConverter;
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
import com.farmers.studyfit.domain.notification.service.NotificationService;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionService {
    private final StudentRepository studentRepository;
    private final ConnectionRepository connectionRepository;
    private final ClassTimeRepository classTimeRepository;
    private final CalendarRepository calendarRepository;
    private final MemberService memberService;
    private final S3Service s3Service;
    private final NotificationService notificationService;
    private final DateConverter dateConverter;

    public SearchStudentResponseDto findStudentByLoginId(String loginId) {
        Student student = studentRepository.findByLoginId(loginId).orElseThrow();
        return SearchStudentResponseDto.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .studentInfo(student.getSchool()+student.getGrade()).build();

    }

    @Transactional
    public void requestConnection(RequestConnectionRequestDto requestConnectionRequestDto) {
        Student student = studentRepository.findById(requestConnectionRequestDto.getStudentId()).orElseThrow();
        Teacher teacher = memberService.getCurrentTeacherMember();
        Connection connection = Connection.builder()
                .teacher(teacher)
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
        String content = teacher.getName() + "선생님의 연결 요청이 도착하였습니다.";
        notificationService.sendNotification(teacher, student, content);
    }

    @Transactional
    public void acceptConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        connection.setStatus(ConnectionState.ACCEPTED);
        connectionRepository.save(connection);
        LocalDate startDate = connection.getStartDate();
        LocalDate endDate = connection.getEndDate();
        List<ClassTime> classTimeList = classTimeRepository.findByConnectionId(connectionId);
        Teacher teacher = connection.getTeacher();
        Student student = connection.getStudent();
        for (ClassTime c : classTimeList) {
            DayOfWeek targetDay = c.getDay();
            LocalTime startTime = c.getStartTime();
            LocalTime endTime = c.getEndTime();
            LocalDate firstMatchingDate = startDate.with(TemporalAdjusters.nextOrSame(targetDay));

            while (!firstMatchingDate.isAfter(endDate)) {
                Calendar calendar = Calendar.builder()
                        .connection(connection)
                        .teacher(teacher)
                        .student(student)
                        .date(firstMatchingDate)
                        .startTime(startTime)
                        .endTime(endTime)
                        .scheduleType(ScheduleType.CLASS).build();
                calendarRepository.save(calendar);
                firstMatchingDate = firstMatchingDate.plusWeeks(1);
            }
        }
        String content = student.getName() + "학생이 연결 요청을 수락하였습니다.";
        notificationService.sendNotification(student, teacher, content);
    }

    @Transactional
    public void rejectConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
            .orElseThrow(() -> new CustomException(ErrorCode.CONNECTION_NOT_FOUND));
        connection.setStatus(ConnectionState.REJECTED);
        connectionRepository.save(connection);
        String content = connection.getStudent().getName() + "학생이 연결 요청을 거절하였습니다.";
        notificationService.sendNotification(connection.getStudent(), connection.getTeacher(), content);
    }

    @Transactional
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
                    .profileImg(s3Service.getFileUrl(student.getProfileImg()))
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
                    .profileImg(s3Service.getFileUrl(teacher.getProfileImg()))
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteConnection(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId).orElseThrow(()-> new RuntimeException());
        connectionRepository.delete(connection);
    }
}
