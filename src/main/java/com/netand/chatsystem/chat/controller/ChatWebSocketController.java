package com.netand.chatsystem.chat.controller;
import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.dto.UnreadCountDTO;
import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.service.ChatMessageService;
import com.netand.chatsystem.notification.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import java.util.Map;
import java.util.HashMap;


@RestController
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final NotificationDispatchService notificationDispatchService;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageRequestDTO dto) {

        // 메시지 저장
        ChatMessageResponseDTO savedMessage = chatMessageService.sendMessage(dto);

        // 실시간 메시지 전송
        messagingTemplate.convertAndSend("/sub/chatroom/" + dto.getChatRoomId(), savedMessage);

        // unreadCount 계산 및 전송
        ChatMessage chatMessage = chatMessageRepository.findById(savedMessage.getMessageId())
                .orElseThrow(() -> new RuntimeException("채팅 메시지를 찾을 수 없습니다."));

        List<UnreadCountDTO> unreadCounts = chatMessageService.getUnreadCounts(chatMessage);

        for (UnreadCountDTO unread : unreadCounts) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("chatRoomId", unread.getChatRoomId());
            payload.put("userId", unread.getUserId());
            payload.put("unreadMessageCount", unread.getUnreadCount());

            messagingTemplate.convertAndSend("/sub/unread/" + unread.getUserId(), payload);
        }

    }

}