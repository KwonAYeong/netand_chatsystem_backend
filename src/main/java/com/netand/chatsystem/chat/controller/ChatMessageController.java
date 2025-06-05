package com.netand.chatsystem.chat.controller;

import com.netand.chatsystem.chat.dto.ChatMessageFileRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.service.ChatMessageService;
import com.netand.chatsystem.common.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/message")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final S3Uploader s3Uploader;

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

    // 파일 첨부 채팅 메세지 전송
    @PostMapping("/file")
    public ResponseEntity<String> sendFileMessage(
            @ModelAttribute ChatMessageFileRequestDTO dto
    ) {
        String fileUrl = s3Uploader.uploadFile(dto.getFile(), "chat");
        chatMessageService.saveFileMessage(dto.getChatRoomId(), dto.getSenderId(), fileUrl);
        return ResponseEntity.ok(fileUrl);
    }

}
