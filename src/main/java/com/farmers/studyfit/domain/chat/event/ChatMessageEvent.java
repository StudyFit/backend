package com.farmers.studyfit.domain.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageEvent {
    private Long chatRoomId;
    private String sender;
    private String content;
    private String messageType;
    private String senderSessionId;
}
