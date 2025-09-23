// src/main/java/com/farmers/studyfit/domain/notification/repository/FcmTokenRepository.java
package com.farmers.studyfit.domain.notification.repository;

// import com.farmers.studyfit.domain.member.entity.Member; // 제거
import com.farmers.studyfit.domain.common.entity.MemberRole;
import com.farmers.studyfit.domain.notification.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    // 특정 멤버 ID와 역할에 해당하는 모든 FCM 토큰을 조회합니다.
    List<FcmToken> findByMemberIdAndMemberRole(Long memberId, MemberRole memberRole);

    // 특정 토큰 문자열로 FcmToken 엔티티를 조회합니다.
    Optional<FcmToken> findByToken(String token);

    // List<String> 리스트에 포함된 토큰들을 찾아 리스트로 반환 (BatchResponse 실패 처리용)
    List<FcmToken> findByTokenIn(List<String> tokens);

}
