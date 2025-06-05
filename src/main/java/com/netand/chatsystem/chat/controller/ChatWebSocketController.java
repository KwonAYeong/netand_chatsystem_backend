package com.netand.chatsystem.chat.controller;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageRequestDTO dto) {
        // 1. 메시지 저장
        ChatMessageResponseDTO savedMessage = chatMessageService.sendMessage(dto);

        // 2. /topic/chatroom/{chatRoomId} 구독자에게 메시지 전송
        messagingTemplate.convertAndSend(
                "/topic/chatroom/" + dto.getChatRoomId(),
                savedMessage
        );
    }
}
