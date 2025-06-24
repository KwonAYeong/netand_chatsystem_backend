package com.netand.chatsystem.chat.service;

import com.netand.chatsystem.chat.dto.ChatLastReadUpdateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateResponseDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;
import com.netand.chatsystem.chat.dto.*;

import java.util.List;

public interface ChatRoomService {

    ChatRoomCreateResponseDTO createOrGetDmRoom(ChatRoomCreateRequestDTO dto);

    List<ChatRoomListResponseDTO> getDmRoomsByUserId(Long userId);

    List<ChatRoomListResponseDTO> getGroupRoomsByUserId(Long userId);

    void updateLastReadMessage(ChatLastReadUpdateRequestDTO dto);

    GroupChatCreateResponseDTO createGroupChatRoom(GroupChatCreateRequestDTO dto);

    void leaveChatRoom(Long chatRoomId, Long userId);

    void updateChatRoomName(Long chatRoomId, String newName);

    List<GroupChatParticipantDTO> getParticipants(Long userId);

    void inviteUsersToGroupChatRoom(Long chatRoomId, InviteToGroupChatRequestDTO dto);

}
