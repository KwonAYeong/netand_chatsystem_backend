package com.netand.chatsystem.setting.dto;

import lombok.Getter;

@Getter
public class RoomNotifySetRequestDTO {
    private Long userId;
    private Long chatRoomId;
    private String alertType;
}
