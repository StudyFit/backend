package com.farmers.studyfit.domain.notification.service;

import com.farmers.studyfit.domain.common.converter.DateConverter;
import com.farmers.studyfit.domain.connection.entity.Connection;
import com.farmers.studyfit.domain.connection.repository.ConnectionRepository;
import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.notification.entity.Notification;
import com.farmers.studyfit.domain.notification.repository.NotificationRepository;
import com.farmers.studyfit.exception.CustomException;
import com.farmers.studyfit.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.NoSuchElementException; // 예외 처리를 위해 추가

@Service // 서비스 계층임을 나타내는 스프링 어노테이션
@RequiredArgsConstructor // final 필드들을 위한 생성자를 자동으로 생성하여 의존성 주입 (Lombok)
@Slf4j // 로깅을 위해 사용 (Lombok)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ConnectionRepository connectionRepository;
    private final ExpoPushMessageSenderService expoPushMessageSenderService; // FCM → Expo로 변경
    private final DateConverter dateConverter;

    @Transactional
    public Notification createNotification(Member receiver, String senderName, String senderProfileImg, String content) {
        Notification notification = Notification.builder()
                .receiverId(receiver.getId()) // Member 객체에서 ID 추출
                .receiverRole(receiver.getRole()) // Member 객체에서 Role 추출 (Member에 getRole() 필요)
                .senderName(senderName)
                .senderProfileImg(senderProfileImg)
                .content(content)
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForMember(Member receiver) {
        return notificationRepository.findByReceiverIdAndReceiverRoleOrderByCreatedAtDesc(receiver.getId(), receiver.getRole());
    }

    @Transactional
    public void markNotificationAsRead(List<Long> notificationIdList, Member member) {
        for (Long notificationId : notificationIdList) {
            Notification notification = notificationRepository.findByIdAndReceiverIdAndReceiverRole(
                            notificationId, member.getId(), member.getRole()) // ID와 Role 사용
                    .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
            if (!notification.isHasRead()) {
                notification.setHasRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    @Transactional
    public void sendHomeworkNotification(Long connectionId, Member teacher, String date) {
        Connection connection = connectionRepository.findById(connectionId).orElseThrow(()->new CustomException(ErrorCode.CONNECTION_NOT_FOUND));

        Member student = connection.getStudent();
        String content = dateConverter.convertDate(date) + " 숙제를 완료하세요!";
        String title = "StudyFit";
        sendNotification(teacher, student, content);
    }

    public void sendNotification(Member sender, Member receiver, String content){
        Notification savedNotification = createNotification(receiver, sender.getName(), sender.getProfileImg(), content);
        // FCM → Expo Push 서비스로 변경
        expoPushMessageSenderService.sendNotification(receiver, sender.getName(), content, savedNotification.getId());
    }
}