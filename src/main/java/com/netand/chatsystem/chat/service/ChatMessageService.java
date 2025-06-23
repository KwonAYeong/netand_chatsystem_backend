package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.dto.UnreadCountDTO;
import com.netand.chatsystem.chat.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {

    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO dto);

    List<ChatMessageResponseDTO> getMessagesByChatRoomId(Long chatRoomId);

    ChatMessageResponseDTO saveFileMessage(Long chatRoomId, Long senderId, String fileUrl);

    List<UnreadCountDTO> getUnreadCounts(ChatMessage message);

}
