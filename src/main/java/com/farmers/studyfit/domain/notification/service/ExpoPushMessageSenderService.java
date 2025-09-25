package com.farmers.studyfit.domain.notification.service;

import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.notification.entity.FcmToken;
import com.farmers.studyfit.domain.notification.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpoPushMessageSenderService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final FcmTokenRepository fcmTokenRepository;
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    public void sendNotification(Member receiver, String senderName, String content, Long notificationId) {
        // FcmToken 테이블에서 해당 사용자의 토큰 조회
        Optional<FcmToken> fcmTokenOpt = fcmTokenRepository.findByMemberIdAndMemberRole(
                receiver.getId(), receiver.getRole()
        );

        if (fcmTokenOpt.isEmpty()) {
            log.warn("Expo Push Token이 등록되지 않은 사용자입니다. 사용자 ID: {}", receiver.getId());
            return;
        }

        String expoPushToken = fcmTokenOpt.get().getToken();

        if (expoPushToken == null || expoPushToken.isEmpty()) {
            log.warn("Expo Push Token이 비어있습니다. 사용자 ID: {}", receiver.getId());
            return;
        }

        try {
            // Expo Push 메시지 구성
            Map<String, Object> pushMessage = new HashMap<>();
            pushMessage.put("to", expoPushToken);
            pushMessage.put("title", "StudyFit");
            pushMessage.put("body", content);
            pushMessage.put("sound", "default");

            // 추가 데이터 (선택사항)
            Map<String, Object> data = new HashMap<>();
            data.put("notificationId", notificationId.toString());
            data.put("senderName", senderName);
            pushMessage.put("data", data);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Accept-Encoding", "gzip, deflate");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(pushMessage, headers);

            // Expo Push API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Expo Push 알림 전송 성공. 사용자 ID: {}, 응답: {}", receiver.getId(), response.getBody());
            } else {
                log.error("Expo Push 알림 전송 실패. 사용자 ID: {}, 상태코드: {}", receiver.getId(), response.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("Expo Push 알림 전송 중 오류 발생. 사용자 ID: {}, 오류: {}", receiver.getId(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생. 사용자 ID: {}, 오류: {}", receiver.getId(), e.getMessage(), e);
        }
    }
}