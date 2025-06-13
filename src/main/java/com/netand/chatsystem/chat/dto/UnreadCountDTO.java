package com.netand.chatsystem.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnreadCountDTO {

    private Long chatRoomId;
    private Long userId;
    private Long unreadCount;
}
