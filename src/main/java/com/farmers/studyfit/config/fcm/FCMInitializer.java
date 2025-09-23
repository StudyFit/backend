package com.farmers.studyfit.config.fcm;

import java.io.ByteArrayInputStream; // 추가
import java.io.IOException;
import java.nio.charset.StandardCharsets; // 추가

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import com.google.firebase.messaging.FirebaseMessaging; // 추가
import org.springframework.context.annotation.Bean; // 추가
import org.springframework.context.annotation.Configuration; // 추가

@Component
@Slf4j
@Configuration // @Bean 정의를 위해 @Configuration으로 변경하거나 추가
public class FCMInitializer {

    @Value("${fcm.service-account-json}")
    private String firebaseServiceAccountJson;

    private FirebaseApp firebaseApp; // FirebaseApp 인스턴스를 저장할 필드

    @PostConstruct
    public void initialize(){
        try{
            if (FirebaseApp.getApps().isEmpty()) { // 이미 초기화된 경우를 대비
                GoogleCredentials googleCredentials = GoogleCredentials
                        .fromStream(new ByteArrayInputStream(firebaseServiceAccountJson.getBytes(StandardCharsets.UTF_8)));

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(googleCredentials)
                        .build();

                this.firebaseApp = FirebaseApp.initializeApp(options); // 초기화된 인스턴스를 저장
                log.info("Firebase application has been initialized");
            } else {
                this.firebaseApp = FirebaseApp.getInstance(); // 이미 초기화된 인스턴스를 가져옴
                log.info("Firebase application already initialized. Reusing existing instance.");
            }
        } catch (IOException e){
            log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            // 초기화 실패 시 애플리케이션 시작을 막거나 적절히 처리해야 할 수 있습니다.
            throw new IllegalStateException("Failed to initialize Firebase Admin SDK", e);
        }
    }

    // FirebaseMessaging 인스턴스를 스프링 빈으로 등록
    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (firebaseApp == null) {
            // @PostConstruct가 먼저 실행되므로 이 경우는 발생하지 않아야 하지만, 안전을 위해 체크
            initialize();
        }
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
