package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;

import java.util.List;

public interface ChatMessageService {

    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO dto);

    List<ChatMessageResponseDTO> getMessagesByChatRoomId(Long chatRoomId);
}
