package com.netand.chatsystem.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomCreateResponseDTO {

    private Long chatRoomId;
    private boolean created;

}
