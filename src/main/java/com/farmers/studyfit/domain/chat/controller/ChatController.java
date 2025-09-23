package com.farmers.studyfit.domain.chat.controller;

import com.farmers.studyfit.domain.chat.dto.*;
import com.farmers.studyfit.domain.chat.service.ChatService;
import com.farmers.studyfit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import static com.farmers.studyfit.response.Message.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;

    @PostMapping("/rooms")
    public Response createChatRoom(@RequestBody CreateChatRoomRequestDto requestDto) {
        Long chatRoomId = chatService.createChatRoom(requestDto);
        return Response.success(CREATE_CHAT_ROOM_SUCCESS, Map.of("chatRoomId", chatRoomId));
    }

    @GetMapping("/rooms")
    public Response getChatRoomList() {
        List<ChatRoomResponseDto> chatRooms = chatService.getChatRoomList();
        return Response.success(GET_CHAT_ROOM_LIST_SUCCESS, chatRooms);
    }

    @PostMapping("/messages")
    public Response sendMessage(@RequestBody SendMessageRequestDto requestDto) {
        chatService.sendMessage(requestDto);
        return Response.success(SEND_MESSAGE_SUCCESS);
    }

    @GetMapping("/rooms/{chatRoomId}/messages")
    public Response getChatMessages(@PathVariable("chatRoomId") Long chatRoomId) {
        List<ChatMessageResponseDto> messages = chatService.getChatMessages(chatRoomId);
        return Response.success(GET_CHAT_MESSAGES_SUCCESS, messages);
    }

}