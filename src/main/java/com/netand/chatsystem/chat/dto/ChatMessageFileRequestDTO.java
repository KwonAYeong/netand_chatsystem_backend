package com.netand.chatsystem.chat.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ChatMessageFileRequestDTO {

    private Long chatRoomId;
    private Long senderId;
    private MultipartFile file;
}
