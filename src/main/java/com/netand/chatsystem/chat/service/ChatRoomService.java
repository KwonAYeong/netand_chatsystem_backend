package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatLastReadUpdateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateResponseDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;

import java.util.List;

public interface ChatRoomService {

    ChatRoomCreateResponseDTO createOrGetDmRoom(ChatRoomCreateRequestDTO dto);

    List<ChatRoomListResponseDTO> getDmRoomsByUserId(Long userId);

    void updateLastReadMessage(ChatLastReadUpdateRequestDTO dto);

}
