package com.netand.chatsystem.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDTO {
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private String createdAt;
}
