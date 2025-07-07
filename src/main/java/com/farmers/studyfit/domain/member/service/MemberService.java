package com.farmers.studyfit.domain.member.service;

import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
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
                        new CustomException(ErrorCode.MEMBER_NOT_FOUND)
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
                        new CustomException(ErrorCode.MEMBER_NOT_FOUND)
                );
    }
}
