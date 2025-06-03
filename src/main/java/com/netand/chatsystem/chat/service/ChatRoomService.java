package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;

import java.util.List;

public interface ChatRoomService {

    Long createOrGetDmRoom(ChatRoomCreateRequestDTO dto);
    List<ChatRoomListResponseDTO> getDmRoomsByUserId(Long userId);

}
