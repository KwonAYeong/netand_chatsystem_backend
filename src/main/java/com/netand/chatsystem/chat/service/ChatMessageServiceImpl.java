package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.entity.ChatMessage;
import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.chat.repository.ChatMessageRepository;
import com.netand.chatsystem.chat.repository.ChatRoomRepository;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService{

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

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

        return ChatMessageResponseDTO.builder()
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
    public void saveFileMessage(Long chatRoomId, Long senderId, String fileUrl) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(fileUrl)
                .fileUrl(fileUrl)
                .messageType("FILE")
                .build();

        chatMessageRepository.save(message);
    }

}
