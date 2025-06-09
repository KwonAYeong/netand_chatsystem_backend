package com.netand.chatsystem.chat.controller;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.service.ChatMessageService;
import com.netand.chatsystem.chat.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final NotificationDispatchService notificationDispatchService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageRequestDTO dto) {
        // 1. 메시지 저장
        ChatMessageResponseDTO savedMessage = chatMessageService.sendMessage(dto);

        // 2. 알림 전송 (알림 설정에 따라 개별 필터링)
        notificationDispatchService.dispatch(
                savedMessage,
                dto.getChatRoomId(),
                dto.getSenderId()
        );
    }
}
