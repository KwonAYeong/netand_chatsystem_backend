package com.netand.chatsystem.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomListResponseDTO {

    private Long chatRoomId;
    private String chatRoomName; // 상대방 이름
    private String chatRoomType;
    private String receiverProfileImage;
    private String lastMessage;
    private boolean hasUnreadMessage;
    private int unreadMessageCount;
    private String receiverStatus; // 상대방 접속 상태
}
