package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.dto.UnreadCountDTO;
import com.netand.chatsystem.chat.entity.*;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.repository.ChatRoomParticipantRepository;
import com.netand.chatsystem.chat.repository.ChatRoomRepository;
import com.netand.chatsystem.chat.repository.MessageInteractionRepository;
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
    private final MessageInteractionRepository messageInteractionRepository;
    private final NotificationDispatchService notificationDispatchService;

    // 메세지 전송
    @Override
    @Transactional
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO dto) {

        // 채팅방, 보낸 사람 조회
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getChatRoomId()).orElse(null);
        User sender = userRepository.findById(dto.getSenderId()).orElse(null);
        if (chatRoom == null || sender == null) return null;

        // 메세지 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(dto.getContent())
                .messageType(dto.getMessageType())
                .fileUrl(dto.getFileUrl())
                .build();
        chatMessageRepository.save(message);

        // 멘션 처리 (mentionedUserNames가 있으면)
        List<String> mentionedUserNames = dto.getMentionedUserNames();
        if (mentionedUserNames != null && !mentionedUserNames.isEmpty()) {
            for (String name : mentionedUserNames) {

                User mentionedUser = userRepository.findByNameInChatRoom(name, chatRoom.getId()).orElse(null);
                if (mentionedUser == null) continue;

                MessageInteraction interaction = MessageInteraction.builder()
                        .message(message)
                        .user(mentionedUser)
                        .interactionType(InteractionType.MENTION)
                        .createdAt(LocalDateTime.now())
                        .build();
                messageInteractionRepository.save(interaction);
            }
        }

        ChatMessageResponseDTO response = ChatMessageResponseDTO.from(message, mentionedUserNames);

        // SSE 알림 전송
        notificationDispatchService.sendChatNotification(response);

        return response;
    }


    // 채팅 메세지 목록 조회
    @Override
    public List<ChatMessageResponseDTO> getMessagesByChatRoomId(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom)
                .stream()
                .map(msg -> {

                    List<MessageInteraction> mentions =
                            messageInteractionRepository.findByMessageIdAndInteractionType(msg.getId(), InteractionType.MENTION);

                    List<String> mentionedUserNames = mentions.stream()
                            .map(m -> m.getUser().getName())
                            .toList();

                    return ChatMessageResponseDTO.from(msg, mentionedUserNames);
                })
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