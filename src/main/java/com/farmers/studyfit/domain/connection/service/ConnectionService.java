package com.farmers.studyfit.domain.connection.service;

import com.farmers.studyfit.domain.connection.dto.*;
import com.farmers.studyfit.domain.connection.entity.ClassTime;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.entity.ConnectionState;
import com.farmers.studyfit.domain.connection.repository.ClassTimeRepository;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.exception.ConnectionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ConnectionRepository connectionRepository;
    private final ClassTimeRepository classTimeRepository;
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

    public void acceptConnection(Long studentId, Long teacherId) {
        Connection connection = connectionRepository
                .findByStudentIdAndTeacherIdAndStatus(studentId, teacherId, ConnectionState.REQUESTED)
                .orElseThrow(() -> new ConnectionNotFoundException("연결을 찾을 수 없습니다: " + studentId + teacherId));

        connection.setStatus(ConnectionState.ACCEPTED);
        connectionRepository.save(connection);
    }

    public void rejectConnection(Long studentId, Long teacherId) {
        Connection connection = connectionRepository
                .findByStudentIdAndTeacherIdAndStatus(studentId, teacherId, ConnectionState.REQUESTED)
                .orElseThrow(() -> new ConnectionNotFoundException("연결을 찾을 수 없습니다"));

        connection.setStatus(ConnectionState.REJECTED);
        connectionRepository.save(connection);
    }
    public void setTeacherColor(Long studentId, Long teacherId, String themeColor) {
        Connection connection = connectionRepository
                .findByStudentIdAndTeacherIdAndStatus(studentId, teacherId, ConnectionState.ACCEPTED)
                .orElseThrow(() -> new RuntimeException("요청된 연결이 존재하지 않습니다"));

        connection.setTeacherColor(themeColor);
        connectionRepository.save(connection);
    }

    public List<StudentDto> getAllConnectedStudentsByTeacher() {
        Teacher teacher = memberService.getCurrentTeacherMember();
        Long teacherId = teacher.getId();
        List<Connection> connections = connectionRepository.findByTeacherIdAndStatus(
                teacherId, ConnectionState.ACCEPTED);

        return connections.stream().map(connection -> {
            Student student = connection.getStudent();
            return StudentDto.builder()
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .grade(student.getGrade())
                    .subject(connection.getSubject())
                    .themeColor(connection.getStudentColor())
                    .note(connection.getMemo())
                    .address(connection.getAddress())
                    .friendStatus(connection.getStatus().name()) // "ACCEPTED"
                    .connectId(connection.getId())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<TeacherDto> getAllConnectedTeachersByStudent() {
        Student student = memberService.getCurrentStudentMember();
        Long studentId = student.getId();
        List<Connection> connections = connectionRepository.findByStudentIdAndStatus(
                studentId, ConnectionState.ACCEPTED);

        return connections.stream().map(connection -> {
            Teacher teacher = connection.getTeacher();
            return TeacherDto.builder()
                    .teacherId(teacher.getId())
                    .teacherName(teacher.getName())
                    .subject(connection.getSubject())
                    .themeColor(connection.getStudentColor())
                    .friendStatus(connection.getStatus().name()) // "ACCEPTED"
                    .connectId(connection.getId())
                    .build();
        }).collect(Collectors.toList());
    }
}
