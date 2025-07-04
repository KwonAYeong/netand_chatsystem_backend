package com.netand.chatsystem.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatMessageRequestDTO {

    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String messageType;  // "TEXT", "FILE"
    private String fileUrl;
    private List<String> mentionedUserNames;
}
