package com.farmers.studyfit.domain.notification.entity;

import com.farmers.studyfit.domain.common.entity.MemberRole;
import com.farmers.studyfit.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_tokens") // 테이블 이름은 fcm_tokens로 하겠습니다.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING) // Enum을 DB에 String으로 저장
    @Column(name = "member_role", nullable = false, length = 20)
    private MemberRole memberRole; // 토큰을 소유한 멤버의 역할 (STUDENT, TEACHER)

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token; // FCM 디바이스 토큰 자체 (길이에 유의)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 토큰 업데이트를 위한 편의 메서드 (선택 사항)
    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
