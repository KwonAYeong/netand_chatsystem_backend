package com.netand.chatsystem.chat.dto;

import com.netand.chatsystem.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ChatMessageResponseDTO {

    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String senderProfileImage;
    private String content;
    private String messageType;
    private String fileUrl;
    private LocalDateTime createdAt;
    private List<String> mentionedUserNames;

    // 멘션 있을 때
    public static ChatMessageResponseDTO from(ChatMessage message, List<String> mentionedUserNames) {
        return ChatMessageResponseDTO.builder()
                .messageId(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .senderProfileImage(message.getSender().getProfileImageUrl())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .fileUrl(message.getFileUrl())
                .createdAt(message.getCreatedAt())
                .mentionedUserNames(mentionedUserNames)
                .build();
    }

    // 멘션 없을 때
    public static ChatMessageResponseDTO from(ChatMessage message) {
        return from(message, null);
    }
}