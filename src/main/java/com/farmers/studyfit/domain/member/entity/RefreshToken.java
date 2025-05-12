package com.farmers.studyfit.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String jti;

    @Column(nullable = false)
    private LocalDateTime expiry;

    // Member 대신, memberId만 저장
    @Column(nullable = false)
    private Long memberId;

    // 로그아웃 시 혹은 검색 시 편의를 위해 role도 함께 저장
    @Column(nullable = false, length = 20)
    private String role;  // "STUDENT" or "TEACHER"
}
