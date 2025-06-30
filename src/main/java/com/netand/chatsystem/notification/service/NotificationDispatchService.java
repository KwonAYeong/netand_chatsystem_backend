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
    public void sendChatNotification(ChatMessageResponseDTO messageDto, List<Long> mentionedUserIds) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByChatRoomId(messageDto.getChatRoomId());

        for (ChatRoomParticipant participant : participants) {
            Long receiverId = participant.getUser().getId();
            if (receiverId.equals(messageDto.getSenderId())) continue;

            NotificationDTO dto = NotificationDTO.builder()
                    .chatRoomId(messageDto.getChatRoomId())
                    .senderId(messageDto.getSenderId())
                    .senderName(messageDto.getSenderName())
                    .message(generateNotificationMessage(messageDto))
                    .createdAt(messageDto.getCreatedAt().toString())
                    .mentionedUserIds(mentionedUserIds) 
                    .build();

            notificationService.sendNotification(receiverId, "chat", dto);
        }
    }


    private String generateNotificationMessage(ChatMessageResponseDTO dto) {
        String sender = dto.getSenderName();
        String content = dto.getContent();
        String messageType = dto.getMessageType();
        String fileUrl = dto.getFileUrl();

        if (messageType.equals("TEXT") && content != null && !content.isBlank()) {
            return sender + ": " + content;
        }

        if (messageType.equals("FILE")) {
            if (fileUrl != null && (fileUrl.endsWith(".jpg")
                    || fileUrl.endsWith(".png")
                    || fileUrl.endsWith(".jpeg")
                    || fileUrl.endsWith(".gif")
                )
            ) {
                return sender + "님이 이미지를 보냈습니다.";
            } else {
                return sender + "님이 파일을 보냈습니다.";
            }
        }

        return sender + "님이 메시지를 보냈습니다.";
    }



}
