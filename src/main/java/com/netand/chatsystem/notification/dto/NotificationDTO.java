package com.netand.chatsystem.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationDTO {
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private String createdAt;
    private List<Long> mentionedUserIds;
}
