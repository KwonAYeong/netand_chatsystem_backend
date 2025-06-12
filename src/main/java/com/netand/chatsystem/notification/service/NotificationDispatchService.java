package com.netand.chatsystem.notification.service;

import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import com.netand.chatsystem.chat.repository.ChatRoomParticipantRepository;
import com.netand.chatsystem.notification.dto.NotificationDTO;
import com.netand.chatsystem.setting.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final NotificationService notificationService;

    /**
     * 수신자에 메시지 알림 전송
     */
    public void sendChatNotification(ChatMessageResponseDTO messageDto, Long chatRoomId, Long senderId) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByChatRoomId(chatRoomId);

        for (ChatRoomParticipant participant : participants) {
            Long receiverId = participant.getUser().getId();
            if (receiverId.equals(senderId)) continue;

            NotificationDTO dto = NotificationDTO.builder()
                    .chatRoomId(chatRoomId)
                    .senderId(senderId)
                    .senderName(messageDto.getSenderName())
                    .message(messageDto.getContent())
                    .createdAt(messageDto.getCreatedAt().toString())
                    .build();

            notificationService.sendNotification(receiverId, "chat", dto);
        }
    }


}
