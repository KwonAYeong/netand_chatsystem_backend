package com.netand.chatsystem.user.dto;

import com.netand.chatsystem.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MentionMessageResDTO {
    private Long messageId;
    private Long chatRoomId;
    private String chatRoomName;
    private String senderName;
    private String senderProfileImageUrl;
    private String content;
    private LocalDate createdAt;

    public static MentionMessageResDTO from(ChatMessage message) {
        return MentionMessageResDTO.builder()
                .messageId(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .chatRoomName(message.getChatRoom().getChatRoomName())
                .senderName(message.getSender().getName())
                .senderProfileImageUrl(message.getSender().getProfileImageUrl())
                .content(message.getContent())
                .createdAt(message.getCreatedAt().toLocalDate())
                .build();
    }
}

