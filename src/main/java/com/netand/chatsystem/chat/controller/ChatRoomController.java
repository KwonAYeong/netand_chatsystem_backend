package com.netand.chatsystem.chat.controller;

import com.netand.chatsystem.chat.dto.*;
import com.netand.chatsystem.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 1:1 채팅방 생성
    @PostMapping("/dm")
    public Long createOrGetChatRoom(@RequestBody ChatRoomCreateRequestDTO dto) {
        return chatRoomService.createOrGetDmRoom(dto);
    }

    // 그룹 채팅방 생성
    @PostMapping("/group")
    public ResponseEntity<GroupChatCreateResponseDTO> createGroupChatRoom(
            @RequestBody GroupChatCreateRequestDTO dto
    ) {
        GroupChatCreateResponseDTO response = chatRoomService.createGroupChatRoom(dto);
        return ResponseEntity.ok(response);
    }

    // 현재 유저 기준 참여한 DM 목록 조회
    @GetMapping("/dm/list/{userId}")
    public List<ChatRoomListResponseDTO> getDmRooms(@PathVariable Long userId) {
        return chatRoomService.getDmRoomsByUserId(userId);
    }

    // 참여한 그룹 채팅 목록 조회
    @GetMapping("/group/list/{userId}")
    public ResponseEntity<List<ChatRoomListResponseDTO>> getGroupChatRooms(
            @PathVariable Long userId
    ) {
        List<ChatRoomListResponseDTO> groupRooms = chatRoomService.getGroupRoomsByUserId(userId);
        return ResponseEntity.ok(groupRooms);
    }

    // 사용자가 채팅방에 입장했을 때, 해당 채팅방의 마지막 메시지를 읽은 것으로 처리
    @PatchMapping("/last-read-message")
    public ResponseEntity<Void> updateLastReadMessage(@RequestBody ChatLastReadUpdateRequestDTO dto) {
        chatRoomService.updateLastReadMessage(dto);
        return ResponseEntity.noContent().build();
    }
}

