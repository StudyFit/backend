package com.farmers.studyfit.domain.connection.service;

import com.farmers.studyfit.domain.connection.dto.ClassTimeDto;
import com.farmers.studyfit.domain.connection.dto.RequestConnectionRequestDto;
import com.farmers.studyfit.domain.connection.dto.SearchStudentResponseDto;
import com.farmers.studyfit.domain.connection.entity.ClassTime;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.entity.ConnectionState;
import com.farmers.studyfit.domain.connection.repository.ClassTimeRepository;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.domain.member.service.AuthService;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .teacher(teacherRepository.findById(1L).orElseThrow())
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
}
