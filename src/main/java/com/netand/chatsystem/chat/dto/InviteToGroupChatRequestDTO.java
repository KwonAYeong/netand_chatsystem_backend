package com.netand.chatsystem.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class InviteToGroupChatRequestDTO {
    private List<String> inviteEmails;
}
