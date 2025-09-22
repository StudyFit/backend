package com.farmers.studyfit.domain.notification.controller;

import com.farmers.studyfit.domain.member.entity.Member;
import com.farmers.studyfit.domain.member.service.MemberService;
import com.farmers.studyfit.domain.notification.service.NotificationService;
import com.farmers.studyfit.response.Message;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final MemberService memberService;

    @PostMapping("/homework/{connectionId}")
    public Response sendHomeworkNotification(
            @PathVariable("connectionId") Long connectionId, @RequestParam("date") String date){
        Member member = memberService.getCurrentTeacherMember();
        notificationService.sendHomeworkNotification(connectionId, member, date);
        return Response.success(Message.POST_ALARM);
    }

    @PatchMapping
    public Response changeNotificationRead(@RequestBody List<Long> notificationIdList){
        Member member = memberService.getCurrentMember();
        notificationService.markNotificationAsRead(notificationIdList, member);
        return Response.success(Message.MARK_ALARM);
    }

    @GetMapping
    public Response getNotifications(){
        Member member = memberService.getCurrentMember();
        return Response.success(Message.GET_ALARM, notificationService.getNotificationsForMember(member));
    }

    @PostMapping("/fcm-token")
    public Response registerFcmToken(@RequestParam("fcmToken") String fcmToken) {
        Member currentMember = memberService.getCurrentMember();
        memberService.registerFcmToken(currentMember, fcmToken);
        return Response.success(Message.FCM_TOKEN_REGISTERED);
    }
}
