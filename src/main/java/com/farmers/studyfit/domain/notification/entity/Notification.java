package com.farmers.studyfit.domain.notification.entity;

import com.farmers.studyfit.domain.common.entity.MemberRole;
import com.farmers.studyfit.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING) // Enum을 DB에 String으로 저장
    @Column(name = "receiver_role", nullable = false, length = 20)
    private MemberRole receiverRole; // 알림을 받은 멤버의 역할 (STUDENT, TEACHER)

    @Column(name = "sender_name", length = 50)
    private String senderName; // 보낸 사람 이름 (스냅샷)

    @Column(name = "sender_profile_img", length = 255)
    private String senderProfileImg; // 보낸 사람 프로필 이미지 URL

    @Column(columnDefinition = "TEXT")
    private String content;  // 알림 본문

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 시각

    @Column(name = "has_read", nullable = false)
    private boolean hasRead;  // 읽음 여부

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.hasRead = false; // 기본값: 안 읽음
    }
}
