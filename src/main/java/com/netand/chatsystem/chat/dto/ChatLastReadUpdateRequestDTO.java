package com.netand.chatsystem.chat.dto;

import lombok.Getter;

@Getter
public class ChatLastReadUpdateRequestDTO {

    private Long userId;
    private Long chatRoomId;
}
