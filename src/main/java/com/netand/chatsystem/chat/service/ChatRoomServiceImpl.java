package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatLastReadUpdateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateResponseDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;
import com.netand.chatsystem.chat.dto.*;
import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.repository.ChatRoomParticipantRepository;
import com.netand.chatsystem.chat.repository.ChatRoomRepository;
import com.netand.chatsystem.common.websocket.UserSessionManager;
import com.netand.chatsystem.setting.entity.NotificationSetting;
import com.netand.chatsystem.setting.repository.NotificationSettingRepository;
import com.netand.chatsystem.setting.service.NotificationSettingService;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserSessionManager userSessionManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationSettingService notificationSettingService;

    // 1:1 채팅방 생성
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
            return new ChatRoomCreateResponseDTO(
                    existingRoom.getId(),
                    receiver.getId(),
                    receiver.getName(),
                    receiver.getProfileImageUrl(),
                    false
            );
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
        notificationSettingService.createNotifySetting(receiver, chatRoom);
        notificationSettingService.createNotifySetting(sender, chatRoom);

        // receiver에게 채팅방 리스트 갱신 트리거 전송
        messagingTemplate.convertAndSend("/sub/chatroom/list/" + receiver.getId(), "REFRESH");

        return new ChatRoomCreateResponseDTO(
                chatRoom.getId(),
                receiver.getId(),
                receiver.getName(),
                receiver.getProfileImageUrl(),
                true
        );
    }


    // 그룹 채팅방 생성
    @Override
    @Transactional
    public GroupChatCreateResponseDTO createGroupChatRoom(GroupChatCreateRequestDTO dto) {
        // 중복 이메일 제거
        Set<String> uniqueParticipantEmails = new HashSet<>(dto.getParticipantEmails());

        // 참여자 수 유효성 검사 (최소 2명 이상)
        if (uniqueParticipantEmails.size() < 2) {
            throw new IllegalArgumentException("그룹 채팅은 최소 2명 이상 참여해야 합니다.");
        }

        // 유효하지 않은 이메일이 포함되어 있는지 확인
        List<User> participants = userRepository.findByEmailIn(uniqueParticipantEmails);
        if (participants.size() != uniqueParticipantEmails.size()) {
            throw new IllegalArgumentException("참여자 중 유효하지 않은 이메일이 포함되어 있습니다.");
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
            // 채팅방 알림 설정 생성
            notificationSettingService.createNotifySetting(user, chatRoom);
        }

        messagingTemplate.convertAndSend("/sub/chatroom/participants/" + chatRoom.getId(), "REFETCH");

        // 모든 참여자에게 채팅방 리스트 갱신 트리거 전송
        for (User user : participants) {
            messagingTemplate.convertAndSend("/sub/chatroom/list/" + user.getId(), "REFRESH");

        }

        return new GroupChatCreateResponseDTO(chatRoom.getId());
    }


    // 그룹채팅방에 사용자 초대
    @Override
    @Transactional
    public void inviteUsersToGroupChatRoom(Long chatRoomId, InviteToGroupChatRequestDTO dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        if (!"GROUP".equals(chatRoom.getChatRoomType())) {
            throw new IllegalStateException("해당 채팅방은 그룹 채팅방이 아닙니다.");
        }

        Set<String> inviteEmails = new HashSet<>(dto.getInviteEmails());
        List<User> usersToInvite = userRepository.findByEmailIn(inviteEmails);

        if (usersToInvite.size() != inviteEmails.size()) {
            throw new IllegalArgumentException("유효하지 않은 이메일이 포함되어 있습니다.");
        }

        for (User user : usersToInvite) {
            Optional<ChatRoomParticipant> existing = chatRoomParticipantRepository
                    .findByChatRoomIdAndUserId(chatRoomId, user.getId());

            if (existing.isPresent()) {
                ChatRoomParticipant participant = existing.get();
                if (participant.getLeftAt() == null) {
                    // 이미 참여 중인 사용자 → 초대 생략
                    continue;
                }

                // 퇴장했던 사용자 재참여 처리
                participant.setLeftAt(null);
                participant.setJoinedAt(LocalDateTime.now());
                chatRoomParticipantRepository.save(participant);

            } else {
                // 새로운 사용자 신규 참여자 등록
                ChatRoomParticipant newParticipant = ChatRoomParticipant.builder()
                        .chatRoom(chatRoom)
                        .user(user)
                        .joinedAt(LocalDateTime.now())
                        .build();
                chatRoomParticipantRepository.save(newParticipant);
                notificationSettingService.createNotifySetting(user, chatRoom);
            }

            // 실시간 채팅방 리스트 갱신 요청
            messagingTemplate.convertAndSend("/sub/chatroom/list/" + user.getId(), "REFRESH");

            messagingTemplate.convertAndSend("/sub/chatroom/participants/" + chatRoomId, "REFETCH");

        }
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


            // 사용자 접속상태값 분기처리
            boolean isSettingOnline = opponent.isActive(); // DB 설정값
            System.out.println("ChatRoomServiceImpl.getDmRoomsByUserId");
            boolean isConnected = userSessionManager.isOnline(opponent.getId()); // 실시간 접속 상태값
            String receiverStatus = (isSettingOnline && isConnected) ? "ONLINE" : "AWAY";

            return ChatRoomListResponseDTO.builder()
                    .chatRoomId(chatRoom.getId())
                    .userId(opponent.getId())
                    .chatRoomName(opponent.getName())
                    .chatRoomType(chatRoom.getChatRoomType())
                    .receiverProfileImage(opponent.getProfileImageUrl())
                    .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                    .hasUnreadMessage(unreadCount > 0)
                    .unreadMessageCount(unreadCount)
                    .receiverStatus(receiverStatus)
                    .build();
        }).toList();
    }

    // 그룹 채팅방 목록 조회
    @Override
    public List<ChatRoomListResponseDTO> getGroupRoomsByUserId(Long userId) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByUserIdAndLeftAtIsNull(userId);

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

    // 그룹 채팅방 나가기
    @Override
    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findWithLockByChatRoomIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));

        participant.leave();
        chatRoomParticipantRepository.save(participant);

        // 나간 사용자에게 채팅방 리스트 리프레시
        messagingTemplate.convertAndSend("/sub/chatroom/list/" + userId, "REFRESH");

        // 남은 유저 수 확인
        int remaining = chatRoomParticipantRepository.countByChatRoomIdAndLeftAtIsNull(chatRoomId);
        if (remaining == 0) {
            notificationSettingRepository.deleteByChatRoomId(chatRoomId);
            chatRoomRepository.delete(chatRoom);
        } else {
            // 남은 참여자들에게도 채팅방 리스트 리프레시
            List<ChatRoomParticipant> others = chatRoomParticipantRepository
                    .findByChatRoomIdAndLeftAtIsNull(chatRoomId)
                    .stream()
                    .filter(p -> !p.getUser().getId().equals(userId))
                    .toList();

            for (ChatRoomParticipant other : others) {
                messagingTemplate.convertAndSend("/sub/chatroom/list/" + other.getUser().getId(), "REFRESH");
            }

            messagingTemplate.convertAndSend("/sub/chatroom/participants/" + chatRoomId, "REFETCH");
        }
    }



    // 그룹채팅방 이름 변경
    @Override
    @Transactional
    public void updateChatRoomName(Long chatRoomId, String newName) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        if (!"GROUP".equals(chatRoom.getChatRoomType())) {
            throw new IllegalStateException("1:1 채팅방 이름은 변경할 수 없습니다.");
        }

        chatRoom.updateName(newName);

        List<ChatRoomParticipant> participants = chatRoomParticipantRepository
                .findByChatRoomIdAndLeftAtIsNull(chatRoomId);

        for (ChatRoomParticipant participant : participants) {
            messagingTemplate.convertAndSend("/sub/chatroom/list/" + participant.getUser().getId(), "REFRESH");
        }
    }

    // 그룹채팅방 참여 인원 조회
    @Override
    public List<GroupChatParticipantDTO> getParticipants(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByChatRoomIdAndLeftAtIsNull(chatRoomId);

        return participants.stream()
                .map(p -> new GroupChatParticipantDTO(
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getUser().getProfileImageUrl()
                ))
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

        ChatMessage lastMessage = null;

        if (dto.getLastReadMessageId() != null) {
            lastMessage = chatMessageRepository.findById(dto.getLastReadMessageId())
                    .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
        } else {
            lastMessage = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom);
        }

        if (lastMessage != null) {
            ChatMessage current = participant.getLastReadMessage();
            if (current == null || current.getId() < lastMessage.getId()) {
                participant.setLastReadMessage(lastMessage);

                chatRoomParticipantRepository.save(participant);
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
