package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatLastReadUpdateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateResponseDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;
import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.repository.ChatRoomParticipantRepository;
import com.netand.chatsystem.chat.repository.ChatRoomRepository;
import com.netand.chatsystem.setting.entity.NotificationSetting;
import com.netand.chatsystem.setting.repository.NotificationSettingRepository;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    @Transactional
    public ChatRoomCreateResponseDTO createOrGetDmRoom(ChatRoomCreateRequestDTO dto) {
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 정보가 존재하지 않습니다."));

        User receiver = userRepository.findByEmail(dto.getReceiverEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일과 일치하는 유저 정보가 존재하지 않습니다."));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("본인과는 채팅할 수 없습니다.");
        }

        ChatRoom existingRoom = chatRoomRepository.findExistingDmRoom(sender.getId(), receiver.getId());
        if (existingRoom != null) {
            return new ChatRoomCreateResponseDTO(existingRoom.getId(), false);
        }

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(receiver.getName())
                .chatRoomType("DM")
                .build();
        chatRoomRepository.save(chatRoom);

        // sender/receiver 동시에 insert 막기 위한 락 + 조건 분기
        saveParticipantIfNotExists(chatRoom, sender);
        saveParticipantIfNotExists(chatRoom, receiver);

        // 채팅방 알림설정 생성
        createChatRoomNotifySetting(sender, chatRoom);
        createChatRoomNotifySetting(receiver, chatRoom);

        return new ChatRoomCreateResponseDTO(chatRoom.getId(), true);
    }



    @Override
    @Transactional
    public List<ChatRoomListResponseDTO> getDmRoomsByUserId(Long userId) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByUserId(userId);

        return participants.stream().map(participant -> {
            ChatRoom chatRoom = participant.getChatRoom();

            User opponent = chatRoom.getParticipants().stream()
                    .map(ChatRoomParticipant::getUser)
                    .filter(user -> !user.getId().equals(userId))
                    .findFirst()
                    .orElseThrow();

            ChatMessage lastMessage = chatMessageRepository
                    .findTopByChatRoomOrderByCreatedAtDesc(chatRoom);

            Long lastReadId = participant.getLastReadMessage() != null
                    ? participant.getLastReadMessage().getId()
                    : 0L;

            int unreadCount = lastMessage != null
                    ? chatMessageRepository.countByChatRoomAndIdGreaterThanAndSenderNot(chatRoom, lastReadId, participant.getUser())
                    : 0;

            return ChatRoomListResponseDTO.builder()
                    .chatRoomId(chatRoom.getId())
                    .chatRoomName(opponent.getName())
                    .chatRoomType(chatRoom.getChatRoomType())
                    .receiverProfileImage(opponent.getProfileImageUrl())
                    .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                    .hasUnreadMessage(unreadCount > 0)
                    .unreadMessageCount(unreadCount)
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public void updateLastReadMessage(ChatLastReadUpdateRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findWithLockByChatRoomIdAndUserId(chatRoom.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다."));

        ChatMessage lastMessage = chatMessageRepository
                .findTopByChatRoomOrderByCreatedAtDesc(chatRoom);

        if (lastMessage != null) {
            ChatMessage current = participant.getLastReadMessage();
            if (current == null || current.getId() < lastMessage.getId()) {
                participant.setLastReadMessage(lastMessage);
            }
        }
    }


    private void createChatRoomNotifySetting(User participantUser, ChatRoom chatRoom) {
        NotificationSetting chatRoomNotifySetting = NotificationSetting.builder()
                .user(participantUser)
                .chatRoom(chatRoom)
                .alertType("ALL")
                .notificationStartTime(LocalTime.of(8, 0))
                .notificationEndTime(LocalTime.of(22, 0))
                .build();
        notificationSettingRepository.save(chatRoomNotifySetting);
    }

    private void saveParticipantIfNotExists(ChatRoom chatRoom, User user) {
        boolean exists = chatRoomParticipantRepository
                .findWithLockByChatRoomIdAndUserId(chatRoom.getId(), user.getId())
                .isPresent();

        if (!exists) {
            chatRoomParticipantRepository.save(
                    ChatRoomParticipant.builder()
                            .chatRoom(chatRoom)
                            .user(user)
                            .joinedAt(LocalDateTime.now())
                            .build()
            );
        }
    }


}
