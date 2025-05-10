package com.farmers.studyfit.security;

import com.farmers.studyfit.config.jwt.TokenProvider;
import com.farmers.studyfit.domain.member.service.MemberDetailsService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final MemberDetailsService memberDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 1. Authorization 헤더 가져오기
        String header = request.getHeader("Authorization");

        // 2. "Bearer "로 시작하는지 확인
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 3. 토큰 유효성 검사
            if (tokenProvider.validateToken(token)) {
                // 4. 토큰에서 클레임(Subject)을 파싱
                Claims claims = tokenProvider.parseClaims(token);
                Long memberId = Long.valueOf(claims.getSubject());

                // 5. UserDetails 조회
                UserDetails userDetails = memberDetailsService.loadUserById(memberId);

                // 6. 인증 객체 생성 및 SecurityContext에 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 7. 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}

