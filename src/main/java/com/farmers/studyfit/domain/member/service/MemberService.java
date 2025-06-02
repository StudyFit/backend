package com.farmers.studyfit.domain.member.service;

import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;


    public Teacher getCurrentTeacherMember() {
        String loginId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return teacherRepository
                .findByLoginId(loginId)
                .orElseThrow(() ->
                        new UserNotFoundException("사용자를 찾을 수 없습니다.")
                );
    }

    public Student getCurrentStudentMember(){
        String loginId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return studentRepository
                .findByLoginId(loginId)
                .orElseThrow(() ->
                        new UserNotFoundException("사용자를 찾을 수 없습니다.")
                );
    }
}
