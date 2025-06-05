package com.netand.chatsystem.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
}
