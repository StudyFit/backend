// src/main/java/com/farmers/studyfit/domain/notification/repository/NotificationRepository.java
package com.farmers.studyfit.domain.notification.repository;

// import com.farmers.studyfit.domain.member.entity.Member; // 제거
import com.farmers.studyfit.domain.common.entity.MemberRole; // 추가
import com.farmers.studyfit.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 멤버 ID와 역할에 해당하는 모든 알림을 최신순으로 조회합니다.
    List<Notification> findByReceiverIdAndReceiverRoleOrderByCreatedAtDesc(Long receiverId, MemberRole receiverRole);

    // 특정 멤버 ID와 역할에 해당하는 읽지 않은 알림을 최신순으로 조회합니다.
    List<Notification> findByReceiverIdAndReceiverRoleAndHasReadFalseOrderByCreatedAtDesc(Long receiverId, MemberRole receiverRole);

    // 알림 ID, 수신자 ID, 수신자 역할을 통해 특정 알림을 조회합니다.
    Optional<Notification> findByIdAndReceiverIdAndReceiverRole(Long id, Long receiverId, MemberRole receiverRole);
}
