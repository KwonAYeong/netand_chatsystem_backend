package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatLastReadUpdateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;
import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.repository.ChatRoomParticipantRepository;
import com.netand.chatsystem.chat.repository.ChatRoomRepository;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.service.GenericParameterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final GenericParameterService parameterBuilder;

    // 채팅방 생성
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

        chatRoomParticipantRepository.save(ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .joinedAt(LocalDateTime.now())
                .build());

        chatRoomParticipantRepository.save(ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .user(receiver)
                .joinedAt(LocalDateTime.now())
                .build());

        return chatRoom.getId();
    }

    // 채팅방 목록 조회
    @Override
    @Transactional
    public List<ChatRoomListResponseDTO> getDmRoomsByUserId(Long userId) {
        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByUserId(userId);

        return participants.stream().map(participant -> {
            ChatRoom chatRoom = participant.getChatRoom();

            // 상대방 찾기
            User opponent = chatRoom.getParticipants().stream()
                    .map(ChatRoomParticipant::getUser)
                    .filter(user -> !user.getId().equals(userId))
                    .findFirst()
                    .orElseThrow();

            // 가장 최근 메시지
            ChatMessage lastMessage = chatMessageRepository
                    .findTopByChatRoomOrderByCreatedAtDesc(chatRoom);

            if (lastMessage != null) {}

            // 마지막 읽은 메시지 ID
            Long lastReadId = participant.getLastReadMessage() != null
                    ? participant.getLastReadMessage().getId()
                    : 0L;

            // 읽지 않은 메세지 수 계산
            User me = participant.getUser();

            int unreadCount = lastMessage != null
                    ? chatMessageRepository.countByChatRoomAndIdGreaterThanAndSenderNot(chatRoom, lastReadId, me)
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

    // 사용자가 채팅방에 입장했을 때, 해당 채팅방의 마지막 메시지를 읽은 것으로 처리
    @Transactional
    @Override
    public void updateLastReadMessage(ChatLastReadUpdateRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findByChatRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다."));

        ChatMessage lastMessage = chatMessageRepository
                .findTopByChatRoomOrderByCreatedAtDesc(chatRoom);

        if (lastMessage != null) {
            participant.setLastReadMessage(lastMessage);
        }
    }




}
