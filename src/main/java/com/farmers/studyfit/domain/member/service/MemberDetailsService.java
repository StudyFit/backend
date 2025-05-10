package com.farmers.studyfit.domain.member.service;

import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepo;

    public MemberDetailsService(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    /**
     * 스프링 시큐리티 로그인(AuthenticationManager) 시
     * 사용자가 입력한 loginId로 UserDetails를 조회할 때 호출됩니다.
     */
    @Override
    public UserDetails loadUserByUsername(String loginId)
            throws UsernameNotFoundException {
        Member member = memberRepo.findByLoginId(loginId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId)
                );
        return buildUserDetails(member);
    }

    /**
     * JWT 필터에서 토큰의 subject(회원 ID)로 직접 UserDetails를 조회할 때 사용합니다.
     */
    public UserDetails loadUserById(Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("사용자를 찾을 수 없습니다: ID=" + memberId)
                );
        return buildUserDetails(member);
    }

    private UserDetails buildUserDetails(Member member) {
        // Spring Security가 제공하는 User 객체를 사용하거나,
        // 직접 CustomUserDetails 클래스를 만들어도 됩니다.
        return User.builder()
                .username(member.getLoginId())
                .password(member.getPasswordHash())
                .roles(
                        // Member가 Student인지 Teacher인지에 따라 ROLE 설정
                        member instanceof com.farmers.studyfit.domain.member.entity.Student
                                ? "STUDENT" : "TEACHER"
                )
                .build();
    }
}
