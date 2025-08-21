package com.farmers.studyfit.config.jwt;

import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.member.entity.Student;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        // secretKey를 HS256용 SigningKey로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("role", member instanceof Student ? "ROLE_STUDENT" : "ROLE_TEACHER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String jti) {
        return Jwts.builder()
                .setId(jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            // 서명/만료 검증 (예: jjwt)
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            // typ, aud 등으로 "refresh"인지도 추가로 구분하고 싶으면 여기서 검사
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Optional<String> extractJti(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.ofNullable(claims.getId()); // jti 표준 클레임
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
