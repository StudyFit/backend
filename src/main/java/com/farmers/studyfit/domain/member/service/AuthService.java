package com.farmers.studyfit.domain.member.service;

import com.farmers.studyfit.config.jwt.TokenProvider;
import com.farmers.studyfit.domain.member.dto.LoginRequestDto;
import com.farmers.studyfit.domain.member.dto.StudentSignUpRequestDto;
import com.farmers.studyfit.domain.member.dto.TeacherSignUpRequestDto;
import com.farmers.studyfit.domain.member.dto.TokenResponseDto;
import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.member.entity.RefreshToken;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.domain.member.entity.Teacher;
import com.farmers.studyfit.domain.member.repository.RefreshTokenRepository;
import com.farmers.studyfit.domain.member.repository.StudentRepository;
import com.farmers.studyfit.domain.member.repository.TeacherRepository;
import com.farmers.studyfit.exception.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUpStudent(StudentSignUpRequestDto dto) {
        checkLoginIdDuplicateCheck(dto.getLoginId());
        Student s = Student.builder()
                .loginId(dto.getLoginId())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .birth(dto.getBirth())
                .phoneNumber(dto.getPhoneNumber())
                .school(dto.getSchool())
                .grade(dto.getGrade())
                .build();
        studentRepository.save(s);
    }

    @Transactional
    public void signUpTeacher(TeacherSignUpRequestDto dto) {
        checkLoginIdDuplicateCheck(dto.getLoginId());
        Teacher t = Teacher.builder()
                .loginId(dto.getLoginId())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .birth(dto.getBirth())
                .phoneNumber(dto.getPhoneNumber())
                .build();
        teacherRepository.save(t);
    }

    private void checkLoginIdDuplicateCheck(String loginId){
        if(studentRepository.existsByLoginId(loginId)|| teacherRepository.existsByLoginId(loginId)){
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_EXCEPTION);
        }
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {
        Member member = studentRepository.findByLoginId(dto.getLoginId())
                .map(s -> (Member)s)
                .orElseGet(() ->
                        teacherRepository.findByLoginId(dto.getLoginId())
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))
                );

        if (!passwordEncoder.matches(dto.getPassword(), member.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 발급·저장 로직 (이전과 동일)
        String access  = tokenProvider.createAccessToken(member);
        String jti     = UUID.randomUUID().toString();
        String refresh = tokenProvider.createRefreshToken(jti);

        refreshTokenRepository.deleteAllByMemberId(member.getId());
        RefreshToken rt = RefreshToken.builder()
                .jti(jti)
                .expiry(
                        LocalDateTime.ofInstant(
                                Instant.now().plusMillis(tokenProvider.getRefreshExpirationMs()),
                                ZoneId.systemDefault()
                        )
                )
                .memberId(member.getId())
                .role(member instanceof Student ? "STUDENT" : "TEACHER")
                .build();
        refreshTokenRepository.save(rt);


        String role = member instanceof Student ? "STUDENT" : "TEACHER";
        return new TokenResponseDto(access, refresh, role);
    }

    @Transactional
    public void logout(String accessToken) {
        Claims claims = tokenProvider.parseClaims(accessToken);
        Long memberId = Long.valueOf(claims.getSubject());

        // 학생 테이블에서 찾고, 없으면 선생님 테이블에서 찾기
        Optional<Member> memberOpt = studentRepository.findById(memberId)
                .map(s -> (Member) s)
                .or(() -> teacherRepository.findById(memberId)
                        .map(t -> (Member) t)
                );

        Member member = memberOpt
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        refreshTokenRepository.deleteAllByMemberId(member.getId());
    }

    @Transactional
    public TokenResponseDto refreshAccessToken(String jti) {
        // 1) JTI 로 RefreshToken 조회
        RefreshToken stored = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOCKEN));

        // 2) 만료 검사
        if (stored.getExpiry().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(stored.getId());  // 만료된 건 지워 두기
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOCKEN);
        }

        // 3) 회원 조회
        Long memberId = stored.getMemberId();
        Member member = studentRepository.findById(memberId)
                .map(s -> (Member)s)
                .orElseGet(() -> teacherRepository.findById(memberId)
                        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)));

        // 4) 신규 액세스 토큰(짧게) + 신규 리프레시 토큰(회전)
        String newAccess  = tokenProvider.createAccessToken(member);
        String newJti     = UUID.randomUUID().toString();
        String newRefresh = tokenProvider.createRefreshToken(newJti);

        // 5) 기존 리프레시 토큰 무효화(회전)
        refreshTokenRepository.deleteById(stored.getId());
        RefreshToken next = RefreshToken.builder()
                .jti(newJti)
                .expiry(LocalDateTime.ofInstant(
                        Instant.now().plusMillis(tokenProvider.getRefreshExpirationMs()),
                        ZoneId.systemDefault()))
                .memberId(memberId)
                .role(member instanceof Student ? "STUDENT" : "TEACHER")
                .build();
        refreshTokenRepository.save(next);

        // 6) 클라이언트에 새 토큰 반환
        String role = member instanceof Student ? "STUDENT" : "TEACHER";
        return new TokenResponseDto(newAccess, newRefresh, role);
    }

}
