package com.netand.chatsystem.setting.controller;

import com.netand.chatsystem.setting.dto.*;
import com.netand.chatsystem.setting.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification-setting")
@RequiredArgsConstructor
public class NotificationSettingController {
    private final NotificationSettingService notificationSettingService;

    // 개인 알림설정 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<NotificationSettingResponseDTO> getNotificationSetting(
            @PathVariable Long userId
    ) {
        NotificationSettingResponseDTO responseDTO = notificationSettingService.getNotificationSetting(userId);
        return ResponseEntity.ok(responseDTO);
    }

    // 전체 및 맨션 알림 설정
    @PutMapping("/global")
    public ResponseEntity<NotificationSettingResponseDTO> updateGlobalNotification(
            @RequestBody GlobalAlertTypeRequestDTO dto
    ) {
        NotificationSettingResponseDTO responseDTO = notificationSettingService.updateGlobalNotification(dto);
        return ResponseEntity.ok(responseDTO);
    }

    // 시간대별 알림 설정
    @PutMapping("/time")
    public ResponseEntity<NotificationSettingResponseDTO> updateNotificationTime(
            @RequestBody NotificationTimeSettingRequestDTO dto
    ) {
        NotificationSettingResponseDTO responseDTO = notificationSettingService.updateNotificationTime(dto);
        return ResponseEntity.ok(responseDTO);
    }

    // 채팅방 알림설정 정보 조회
    @GetMapping("/{userId}/{chatRoomId}")
    public ResponseEntity<RoomNotifySetResponseDTO> getRoomNotifySetting(
            @PathVariable Long chatRoomId,
            @PathVariable Long userId
    ) {
        RoomNotifySetResponseDTO responseDTO = notificationSettingService.getRoomNotifySetting(userId, chatRoomId);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/chat-room")
    public ResponseEntity<NotificationSettingResponseDTO> updateChatRoomNotification(
            @RequestBody RoomNotifySetRequestDTO dto
    ) {
        NotificationSettingResponseDTO responseDTO = notificationSettingService.updateChatRoomNotification(dto);
        return ResponseEntity.ok(responseDTO);
    }

}
