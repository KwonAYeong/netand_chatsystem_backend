package com.netand.chatsystem.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupChatParticipantDTO {

    private Long userId;
    private String name;
    private String profileImageUrl;
}
