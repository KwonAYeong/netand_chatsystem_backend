package com.netand.chatsystem.chat.dto;

import lombok.Getter;

@Getter
public class ChatRoomCreateRequestDTO {

    private Long senderId;
    private String receiverEmail;
}
