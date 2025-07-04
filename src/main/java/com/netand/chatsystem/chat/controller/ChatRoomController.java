package com.netand.chatsystem.chat.controller;

import com.netand.chatsystem.chat.dto.ChatLastReadUpdateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateRequestDTO;
import com.netand.chatsystem.chat.dto.ChatRoomCreateResponseDTO;
import com.netand.chatsystem.chat.dto.ChatRoomListResponseDTO;
import com.netand.chatsystem.chat.dto.*;
import com.netand.chatsystem.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 1:1 채팅방 생성
    @PostMapping("/dm")
    public ResponseEntity<ChatRoomCreateResponseDTO> createOrGetChatRoom(@RequestBody ChatRoomCreateRequestDTO dto) {
        ChatRoomCreateResponseDTO response = chatRoomService.createOrGetDmRoom(dto);
      
        return ResponseEntity.ok(response);
    }

    // 그룹 채팅방 생성
    @PostMapping("/group")
    public ResponseEntity<GroupChatCreateResponseDTO> createGroupChatRoom(@RequestBody GroupChatCreateRequestDTO dto) {
        GroupChatCreateResponseDTO response = chatRoomService.createGroupChatRoom(dto);

        return ResponseEntity.ok(response);
    }

    // 그룹채팅방에 사용자 초대
    @PostMapping("/{chatRoomId}/invite")
    public ResponseEntity<Void> inviteToGroup(
            @PathVariable Long chatRoomId,
            @RequestBody InviteToGroupChatRequestDTO dto) {
        chatRoomService.inviteUsersToGroupChatRoom(chatRoomId, dto);
        return ResponseEntity.ok().build();
    }


    // 현재 유저 기준 참여한 DM 목록 조회
    @GetMapping("/dm/list/{userId}")
    public List<ChatRoomListResponseDTO> getDmRooms(@PathVariable Long userId) {

        return chatRoomService.getDmRoomsByUserId(userId);
    }

    // 참여한 그룹 채팅 목록 조회
    @GetMapping("/group/list/{userId}")
    public ResponseEntity<List<ChatRoomListResponseDTO>> getGroupChatRooms(@PathVariable Long userId) {
        List<ChatRoomListResponseDTO> groupRooms = chatRoomService.getGroupRoomsByUserId(userId);

        return ResponseEntity.ok(groupRooms);
    }

    // 사용자가 채팅방에 입장했을 때, 해당 채팅방의 마지막 메시지를 읽은 것으로 처리
    @PatchMapping("/last-read-message")
    public ResponseEntity<Void> updateLastReadMessage(@RequestBody ChatLastReadUpdateRequestDTO dto) {
        chatRoomService.updateLastReadMessage(dto);

        return ResponseEntity.noContent().build();
    }

    // 채팅방 나가기
    @DeleteMapping("/{chatRoomId}/leave/{userId}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long chatRoomId, @PathVariable Long userId) {
        chatRoomService.leaveChatRoom(chatRoomId, userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹채팅방 이름 변경
    @PatchMapping("/{chatRoomId}/name")
    public ResponseEntity<Map<String, String>> updateChatRoomName(
            @PathVariable Long chatRoomId,
            @RequestBody Map<String, String> request
    ) {
        String newName = request.get("newName");
        chatRoomService.updateChatRoomName(chatRoomId, newName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "채팅방 이름이 변경되었습니다.");

        return ResponseEntity.ok(response);
    }

    // 그룹채팅방 참여 인원 조회
    @GetMapping("/{chatRoomId}/participants")
    public ResponseEntity<List<GroupChatParticipantDTO>> getParticipants(@PathVariable Long chatRoomId) {
        List<GroupChatParticipantDTO> participants = chatRoomService.getParticipants(chatRoomId);

        return ResponseEntity.ok(participants);
    }




}

