package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.chat.entity.ChatRoomParticipant;
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
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final GenericParameterService parameterBuilder;

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

    @Override
    public List<ChatRoomListResponseDTO> getDmRoomsByUserId(Long userId) {

        return chatRoomParticipantRepository.findChatRoomsByUserId(userId);
    }
}
