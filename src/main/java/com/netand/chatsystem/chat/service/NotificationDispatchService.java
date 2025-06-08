package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import com.netand.chatsystem.chat.repository.ChatRoomParticipantRepository;
import com.netand.chatsystem.setting.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final NotificationSettingService notificationSettingService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅방의 참여자에게 알림 설정에 따라 WebSocket 메시지 전송
     */
    public void dispatch(ChatMessageResponseDTO messageDto, Long chatRoomId, Long senderId) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByChatRoomId(chatRoomId);

        for (ChatRoomParticipant participant : participants) {
            Long receiverId = participant.getUser().getId();

            // 발신자는 제외
            if (receiverId.equals(senderId)) continue;

            // 전체 알림 설정 확인 (alertType == "NONE" 이면 전송 안 함)
            boolean enabled = notificationSettingService.isNotificationEnabled(senderId, chatRoomId);

            if (enabled) {
                messagingTemplate.convertAndSendToUser(
                        receiverId.toString(),
                        "/queue/chat",
                        messageDto
                );
            }
        }
    }

}
