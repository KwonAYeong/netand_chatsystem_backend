package com.netand.chatsystem.chat.controller;

import com.netand.chatsystem.chat.dto.ChatMessageFileRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageRequestDTO;
import com.netand.chatsystem.chat.dto.ChatMessageResponseDTO;
import com.netand.chatsystem.chat.service.ChatMessageService;
import com.netand.chatsystem.common.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 파일 전송
    @PostMapping("/file")
    public ResponseEntity<Map<String, String>> uploadFileOnly(
            @ModelAttribute ChatMessageFileRequestDTO dto
    ) {
        String fileUrl = s3Uploader.uploadFile(dto.getFile(), "chat");

        Map<String, String> response = new HashMap<>();
        response.put("fileUrl", fileUrl);

        return ResponseEntity.ok(response);
    }

    // 파일 다운로드
    @GetMapping("/file/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileUrl) {
        try {
            URL url = new URL(fileUrl); // S3 URL로 부터 스트림 열기
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 파일 이름 추출

            Resource resource = new InputStreamResource(url.openStream());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드 실패", e);
        }
    }


}
