package com.farmers.studyfit.domain.member.service;

import com.farmers.studyfit.domain.S3Service;
import com.farmers.studyfit.domain.member.dto.ChangePasswordRequestDto;
import com.farmers.studyfit.domain.member.dto.ProfileResponseDto;
import com.farmers.studyfit.domain.member.dto.UpdateProfileRequestDto;
import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.domain.notification.entity.FcmToken;
import com.farmers.studyfit.domain.notification.repository.FcmTokenRepository;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final FcmTokenRepository fcmTokenRepository;


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

    public Member getCurrentMember(){
        Member member;
        try{
            member = getCurrentTeacherMember();
        }catch (Exception e){
            member = getCurrentStudentMember();
        }
        return member;
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

    // 마이페이지
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            Teacher teacher = getCurrentTeacherMember();
            return ProfileResponseDto.builder()
                    .id(teacher.getId())
                    .loginId(teacher.getLoginId())
                    .name(teacher.getName())
                    .birth(teacher.getBirth())
                    .phoneNumber(teacher.getPhoneNumber())
                    .profileImg(teacher.getProfileImg())
                    .role("TEACHER")
                    .build();
        } else {
            Student student = getCurrentStudentMember();
            return ProfileResponseDto.builder()
                    .id(student.getId())
                    .loginId(student.getLoginId())
                    .name(student.getName())
                    .birth(student.getBirth())
                    .phoneNumber(student.getPhoneNumber())
                    .profileImg(student.getProfileImg())
                    .role("STUDENT")
                    .school(student.getSchool())
                    .grade(student.getGrade())
                    .build();
        }
    }

    @Transactional
    public void updateProfile(UpdateProfileRequestDto requestDto) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            Teacher teacher = getCurrentTeacherMember();
            teacher.setName(requestDto.getName());
            teacher.setPhoneNumber(requestDto.getPhoneNumber());
            teacherRepository.save(teacher);
        } else {
            Student student = getCurrentStudentMember();
            student.setName(requestDto.getName());
            student.setPhoneNumber(requestDto.getPhoneNumber());
            student.setSchool(requestDto.getSchool());
            student.setGrade(requestDto.getGrade());
            studentRepository.save(student);
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDto requestDto) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            Teacher teacher = getCurrentTeacherMember();
            if (!passwordEncoder.matches(requestDto.getCurrentPassword(), teacher.getPasswordHash())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
            teacher.setPasswordHash(passwordEncoder.encode(requestDto.getNewPassword()));
            teacherRepository.save(teacher);
        } else {
            Student student = getCurrentStudentMember();
            if (!passwordEncoder.matches(requestDto.getCurrentPassword(), student.getPasswordHash())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
            student.setPasswordHash(passwordEncoder.encode(requestDto.getNewPassword()));
            studentRepository.save(student);
        }
    }

    @Transactional
    public String uploadProfileImage(MultipartFile file) throws IOException {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (teacherRepository.findByLoginId(loginId).isPresent()) {
            return uploadTeacherProfileImg(file);
        } else {
            return uploadStudentProfileImg(file);
        }
    }


    @Transactional
    public void registerFcmToken(Member member, String fcmTokenString) {
        // 기존: 토큰으로만 조회
        // Optional<FcmToken> existingToken = fcmTokenRepository.findByToken(fcmTokenString);

        // 수정: 해당 회원의 기존 토큰 조회
        Optional<FcmToken> existingToken = fcmTokenRepository.findByMemberIdAndMemberRole(
                member.getId(), member.getRole()
        );

        if (existingToken.isPresent()) {
            // 기존 토큰이 있으면 새 토큰으로 업데이트
            FcmToken fcmToken = existingToken.get();
            fcmToken.setToken(fcmTokenString);
            fcmToken.setCreatedAt(LocalDateTime.now());
            fcmTokenRepository.save(fcmToken);
        } else {
            // 새 토큰 생성
            FcmToken newFcmToken = FcmToken.builder()
                    .memberId(member.getId())
                    .memberRole(member.getRole())
                    .token(fcmTokenString)
                    .build();
            fcmTokenRepository.save(newFcmToken);
        }
    }
}