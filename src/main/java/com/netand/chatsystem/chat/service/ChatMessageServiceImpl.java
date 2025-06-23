package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.dto.UnreadCountDTO;
import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.repository.ChatRoomParticipantRepository;
import com.netand.chatsystem.chat.repository.ChatRoomRepository;
import com.netand.chatsystem.notification.service.NotificationDispatchService;
import com.netand.chatsystem.notification.service.NotificationService;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService{

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final NotificationDispatchService notificationDispatchService;

    // 메세지 전송
    @Override
    @Transactional
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO dto) {
        
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getChatRoomId())

                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람을 찾을 수 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(dto.getContent())
                .messageType(dto.getMessageType())
                .fileUrl(dto.getFileUrl())
                .build();

        chatMessageRepository.save(message);

        ChatMessageResponseDTO response = ChatMessageResponseDTO.builder()
                .messageId(message.getId())
                .chatRoomId(chatRoom.getId())
                .senderId(sender.getId())
                .senderName(sender.getName())
                .senderProfileImage(sender.getProfileImageUrl())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .fileUrl(message.getFileUrl())
                .createdAt(message.getCreatedAt())
                .build();

        // SSE 알림 전송
        notificationDispatchService.sendChatNotification(response, chatRoom.getId(), sender.getId());

        return response;
    }

    // 채팅 메세지 목록 조회
    @Override
    public List<ChatMessageResponseDTO> getMessagesByChatRoomId(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom)
                .stream()
                .map(msg -> ChatMessageResponseDTO.builder()
                        .messageId(msg.getId())
                        .chatRoomId(chatRoomId)
                        .senderId(msg.getSender().getId())
                        .senderName(msg.getSender().getName())
                        .senderProfileImage(msg.getSender().getProfileImageUrl())
                        .content(msg.getContent())
                        .messageType(msg.getMessageType())
                        .fileUrl(msg.getFileUrl())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessageResponseDTO saveFileMessage(Long chatRoomId, Long senderId, String fileUrl) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .messageType("FILE")
                .fileUrl(fileUrl)
                .content("")
                .build();

        chatMessageRepository.save(message);

        return ChatMessageResponseDTO.from(message);
    }

    @Override
    public List<UnreadCountDTO> getUnreadCounts(ChatMessage message) {
        Long chatRoomId = message.getChatRoom().getId();
        Long messageId = message.getId();

        List<ChatRoomParticipant> participants =
                chatRoomParticipantRepository.findByChatRoomId(chatRoomId);

        List<UnreadCountDTO> unreadCounts = new ArrayList<>();

        for (ChatRoomParticipant participant : participants) {
            Long participantId = participant.getUser().getId();

            if (!participantId.equals(message.getSender().getId())) {

                ChatMessage lastReadMessage = participant.getLastReadMessage();
                Long lastReadMessageId = (lastReadMessage != null) ? lastReadMessage.getId() : null;

                Long count;
                if (lastReadMessageId == null) {
                    count = chatMessageRepository.countByChatRoomIdAndIdGreaterThan(chatRoomId, 0L);
                } else {
                    count = chatMessageRepository.countByChatRoomIdAndIdGreaterThan(chatRoomId, lastReadMessageId);
                }

                unreadCounts.add(new UnreadCountDTO(chatRoomId, participantId, count));
            }
        }

        return unreadCounts;
    }

}