package com.farmers.studyfit.domain.member.service;

import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public MemberDetailsService(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository
    ) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId){
        // 1) 학생 먼저 조회
        Optional<Member> memberOpt = studentRepository.findByLoginId(loginId)
                .map(s -> (Member) s)
                // 2) 없으면 선생님 조회
                .or(() -> teacherRepository.findByLoginId(loginId).map(t -> (Member) t));

        // 3) 없으면 예외
        Member member = memberOpt
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        return buildUserDetails(member);
    }

    public UserDetails loadUserById(Long memberId) {
        Optional<Member> memberOpt = studentRepository.findById(memberId)
                .map(s -> (Member) s)
                .or(()-> teacherRepository.findById(memberId).map(t->(Member)t));

        Member member = memberOpt
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + memberId));

        return buildUserDetails(member);
    }

    private UserDetails buildUserDetails(Member member) {
        return User.builder()
                .username(member.getLoginId())
                .password(member.getPasswordHash())
                .roles(member instanceof Student ? "STUDENT" : "TEACHER")
                .build();
    }
}
