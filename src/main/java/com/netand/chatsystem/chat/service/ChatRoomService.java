package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.*;

import java.util.List;

public interface ChatRoomService {

    Long createOrGetDmRoom(ChatRoomCreateRequestDTO dto);

    List<ChatRoomListResponseDTO> getDmRoomsByUserId(Long userId);

    List<ChatRoomListResponseDTO> getGroupRoomsByUserId(Long userId);

    void updateLastReadMessage(ChatLastReadUpdateRequestDTO dto);

    GroupChatCreateResponseDTO createGroupChatRoom(GroupChatCreateRequestDTO dto);

    void leaveChatRoom(Long chatRoomId, Long userId);

    void updateChatRoomName(Long chatRoomId, String newName);

    List<GroupChatParticipantDTO> getParticipants(Long userId);

}
