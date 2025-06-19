package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.*;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    // 1:1 채팅방 생성
    @Override
    @Transactional
    public Long createOrGetDmRoom(ChatRoomCreateRequestDTO dto) {
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 정보가 존재하지 않습니다."));

        User receiver = userRepository.findByEmail(dto.getReceiverEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일과 일치하는 유저 정보가 존재하지 않습니다."));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("본인과는 채팅할 수 없습니다.");
        }

        ChatRoom existingRoom = chatRoomRepository.findExistingDmRoom(sender.getId(), receiver.getId());
        if (existingRoom != null) {
            return existingRoom.getId();
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(receiver.getName())
                .chatRoomType("DM")
                .build();
        chatRoomRepository.save(chatRoom);

        // sender/receiver 동시에 insert 막기 위한 락 + 조건 분기
        saveParticipantIfNotExists(chatRoom, sender);
        saveParticipantIfNotExists(chatRoom, receiver);

        // 채팅방 알림설정 엔터티 생성
        createChatRoomNotifySetting(sender, chatRoom);
        createChatRoomNotifySetting(receiver, chatRoom);

        return chatRoom.getId();
    }

    // 그룹 채팅방 생성
    @Override
    public GroupChatCreateResponseDTO createGroupChatRoom(GroupChatCreateRequestDTO dto) {
        // 중복 user 제거
        Set<Long> uniqueParticipantIds = new HashSet<>(dto.getParticipantIds());

        // user 수 유효성 검사 (본인 포함 최소 2명 이상)
        if (uniqueParticipantIds.size() < 2) {
            throw new IllegalArgumentException("그룹 채팅은 최소 2명 이상 참여해야 합니다.");
        }

        // 유효하지 않은 userId가 있는지 확인
        List<User> participants = userRepository.findAllById(uniqueParticipantIds);
        if (participants.size() != uniqueParticipantIds.size()) {
            throw new IllegalArgumentException("참여자 중 유효하지 않은 유저가 포함되어 있습니다.");
        }

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(dto.getChatRoomName())
                .chatRoomType("GROUP")
                .build();
        chatRoomRepository.save(chatRoom);

        // 참여자 등록
        for (User user : participants) {
            ChatRoomParticipant participant = ChatRoomParticipant.builder()
                    .chatRoom(chatRoom)
                    .user(user)
                    .joinedAt(LocalDateTime.now())
                    .build();
            chatRoomParticipantRepository.save(participant);
        }

        return new GroupChatCreateResponseDTO(chatRoom.getId());
    }

    // 1:1 채팅방 목록 조회
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

    // 그룹 채팅방 목록 조회
    @Override
    public List<ChatRoomListResponseDTO> getGroupRoomsByUserId(Long userId) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByUserId(userId);

        return participants.stream()
                .filter(p -> "GROUP".equals(p.getChatRoom().getChatRoomType()))
                .map(p -> {
                    ChatRoom room = p.getChatRoom();
                    ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(room);

                    Long lastReadMessageId = p.getLastReadMessage() != null ? p.getLastReadMessage().getId() : 0L;
                    long unreadCount = chatMessageRepository.countByChatRoomIdAndIdGreaterThan(room.getId(), lastReadMessageId);

                    return ChatRoomListResponseDTO.builder()
                            .chatRoomId(room.getId())
                            .chatRoomName(room.getChatRoomName())
                            .chatRoomType("GROUP")
                            .receiverProfileImage(null) // 그룹 채팅은 프로필 이미지 X
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                            .hasUnreadMessage(unreadCount > 0)
                            .unreadMessageCount((int) unreadCount)
                            .build();
                })
                .sorted(Comparator.comparing(ChatRoomListResponseDTO::getUnreadMessageCount).reversed())
                .toList();
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
