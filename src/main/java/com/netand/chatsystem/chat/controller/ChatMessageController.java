package com.netand.chatsystem.chat.controller;

import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/message")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // 채팅 메세지 전송
    @PostMapping
    public ResponseEntity<ChatMessageResponseDTO> sendMessage(@RequestBody ChatMessageRequestDTO dto) {
        ChatMessageResponseDTO response = chatMessageService.sendMessage(dto);
        return ResponseEntity.ok(response);
    }

    // 채팅 메세지 목록 조회
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponseDTO>> getMessages(@PathVariable Long chatRoomId) {
        List<ChatMessageResponseDTO> messages = chatMessageService.getMessagesByChatRoomId(chatRoomId);
        return ResponseEntity.ok(messages);
    }
}
