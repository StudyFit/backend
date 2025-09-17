package com.farmers.studyfit.domain.member.service;

import com.farmers.studyfit.domain.S3Service;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final S3Service s3Service;


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

    @Transactional
    public String uploadStudentProfileImg(MultipartFile file) throws IOException {
        Student student = getCurrentStudentMember();
        String url = s3Service.uploadFile(file);
        student.setProfileImg(url);
        studentRepository.save(student);
        return url;
    }

    @Transactional
    public String uploadTeacherProfileImg(MultipartFile file) throws IOException {
        Teacher teacher = getCurrentTeacherMember();
        String url = s3Service.uploadFile(file);
        teacher.setProfileImg(url);
        teacherRepository.save(teacher);
        return url;
    }
}
