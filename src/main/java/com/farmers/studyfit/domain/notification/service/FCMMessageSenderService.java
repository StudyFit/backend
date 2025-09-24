// src/main/java/com/farmers/studyfit/domain/notification/service/FCMMessageSenderService.java
package com.farmers.studyfit.domain.notification.service;

import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.notification.entity.FcmToken;
import com.farmers.studyfit.domain.notification.repository.FcmTokenRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMMessageSenderService {

    private final FirebaseMessaging firebaseMessaging; // FirebaseMessaging 인스턴스를 주입받습니다.
    private final FcmTokenRepository fcmTokenRepository;

    // FirebaseMessaging 인스턴스는 FCMInitializer에서 초기화된 FirebaseApp으로부터 얻을 수 있습니다.
    // FCMInitializer에 @Bean 메서드를 추가하여 FirebaseMessaging 빈을 등록하는 것이 좋습니다.
    // (아래 추가 설명 참고)

    /**
     * 특정 멤버에게 FCM 푸시 알림을 보냅니다.
     * 해당 멤버의 모든 기기(FCM 토큰)로 메시지가 전송됩니다.
     *
     * @param receiver 알림을 받을 멤버 객체
     * @param title 알림 제목
     * @param body 알림 본문
     * @param notificationId DB에 저장된 알림의 ID (클라이언트 앱에서 상세 페이지로 이동 시 활용)
     * @return 성공적으로 전송된 메시지 개수
     */
    public int sendNotification(Member receiver, String title, String body, Long notificationId) {
        List<String> tokens = fcmTokenRepository.findByMemberIdAndMemberRole(receiver.getId(), receiver.getRole())
                .stream()
                .map(FcmToken::getToken)
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for member ID: {}. Notification not sent.", receiver.getId());
            return 0;
        }

        // 알림 메시지 구성 (Notification payload)
        // Notification payload는 사용자에게 직접 보여지는 알림의 제목, 본문 등을 포함합니다.
        // data payload는 앱이 백그라운드나 포그라운드일 때 추가적인 정보를 처리할 수 있도록 합니다.
        MulticastMessage message = MulticastMessage.builder() // <--- 이 줄을 정확히 확인해주세요!
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setImage("https://study-fit-bucket.s3.ap-northeast-2.amazonaws.com/studyfit-logo.png")
                        .build())
                .putData("notificationId", String.valueOf(notificationId))
                .putData("type", "HOMEWORK_ASSIGNED")
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            log.info("Successfully sent FCM message to member ID {}. Success count: {}, Failure count: {}",
                    receiver.getId(), response.getSuccessCount(), response.getFailureCount());

            if (response.getFailureCount() > 0) {
                // 실패한 토큰들을 처리하는 로직 (예: 유효하지 않은 토큰은 DB에서 삭제)
                response.getResponses().forEach(sendResponse -> {
                    if (!sendResponse.isSuccessful()) {
                        log.warn("Failed to send FCM message: {}. Error: {}", sendResponse.getMessageId(), sendResponse.getException().getMessage());
                        // 여기서 sendResponse.getException().getMessagingErrorCode() 등을 확인하여 토큰 삭제 로직 구현 가능
                        // 예: NotRegistered 에러는 토큰이 만료되었거나 유효하지 않음을 의미
                    }
                });
            }
            return response.getSuccessCount();

        } catch (FirebaseMessagingException e) {
            log.error("Error sending FCM message to member ID {}: {}", receiver.getId(), e.getMessage(), e);
            return 0;
        }
    }

    /**
     * FCM 토큰이 유효하지 않을 경우 데이터베이스에서 삭제하는 메서드 (선택 사항)
     * 실제 서비스에서는 sendNotification 내부에서 처리하는 것이 일반적입니다.
     */
    public void removeInvalidTokens(List<String> invalidTokens) {
        fcmTokenRepository.findByToken(invalidTokens.get(0)).ifPresent(fcmTokenRepository::delete);
        // 이 로직은 단일 토큰 처리 예시이며, BatchResponse 처리 시 여러 토큰을 한번에 처리하도록 개선 필요
    }
}
