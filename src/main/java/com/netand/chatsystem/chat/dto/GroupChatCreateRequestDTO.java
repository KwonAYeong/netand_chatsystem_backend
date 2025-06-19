package com.netand.chatsystem.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupChatCreateRequestDTO {

    private String chatRoomName;
    private List<Long> participantIds;
}
