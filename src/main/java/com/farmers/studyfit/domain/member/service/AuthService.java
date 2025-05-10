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
import com.farmers.studyfit.domain.member.repository.MemberRepository;
import com.farmers.studyfit.domain.member.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepo;
    private final RefreshTokenRepository refreshRepo;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUpStudent(StudentSignUpRequestDto dto) {
        if (memberRepo.existsByLoginId(dto.getLoginId())) {
            throw new RuntimeException();
            //throw new BadRequestException("이미 사용 중인 로그인 아이디입니다.");
        }
        Student student = Student.builder()
                .loginId(dto.getLoginId())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .birth(dto.getBirth())
                .phoneNumber(dto.getPhoneNumber())
                .school(dto.getSchool())
                .grade(dto.getGrade())
                .build();
        memberRepo.save(student);
    }

    @Transactional
    public void signUpTeacher(TeacherSignUpRequestDto dto) {
        if (memberRepo.existsByLoginId(dto.getLoginId())) {
            throw new RuntimeException();
            //throw new BadRequestException("이미 사용 중인 로그인 아이디입니다.");
        }
        Teacher teacher = Teacher.builder()
                .loginId(dto.getLoginId())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .birth(dto.getBirth())
                .phoneNumber(dto.getPhoneNumber())
                .build();
        memberRepo.save(teacher);
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {
        Member m = memberRepo.findByLoginId(dto.getLoginId())
                .orElseThrow(RuntimeException::new);
                //.orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        if (!passwordEncoder.matches(dto.getPassword(), m.getPasswordHash())) {
            throw new RuntimeException();
            //throw new BadCredentialsException("비밀번호 불일치");
        }

        // 1) 액세스 토큰
        String access = tokenProvider.createAccessToken(m);
        // 2) 리프레시 토큰 (jti 는 UUID)
        String jti = UUID.randomUUID().toString();
        String refresh = tokenProvider.createRefreshToken(jti);

        // 3) 기존 리프레시 전부 삭제 + 새로 저장
        refreshRepo.deleteAllByMember(m);
        RefreshToken rt = RefreshToken.builder()
                .jti(jti)
                .expiry(
                        LocalDateTime.now()
                                .plus(tokenProvider.getRefreshExpirationMs(), ChronoUnit.MILLIS)
                )
                .member(m)
                .build();
        refreshRepo.save(rt);

        return new TokenResponseDto(access, refresh);
    }

    @Transactional
    public void logout(String accessToken) {
        // 액세스 토큰에서 회원 ID 추출
        Claims claims = tokenProvider.parseClaims(accessToken);
        Long memberId = Long.valueOf(claims.getSubject());
        Member m = memberRepo.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원 없음"));
        // 해당 회원의 모든 리프레시 토큰 삭제 → 즉시 로그아웃
        refreshRepo.deleteAllByMember(m);
    }
}
