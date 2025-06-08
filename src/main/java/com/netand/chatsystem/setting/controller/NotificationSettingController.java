package com.netand.chatsystem.setting.controller;

import com.netand.chatsystem.setting.dto.GlobalAlertTypeRequestDTO;
import com.netand.chatsystem.setting.dto.NotificationSettingResponseDTO;
import com.netand.chatsystem.setting.dto.NotificationTimeSettingRequestDTO;
import com.netand.chatsystem.setting.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification-setting")
@RequiredArgsConstructor
public class NotificationSettingController {
    private final NotificationSettingService notificationSettingService;

    @GetMapping("/{userId}")
    public ResponseEntity<NotificationSettingResponseDTO> getNotificationSetting(
            @PathVariable Long userId
    ) {
        NotificationSettingResponseDTO dto = notificationSettingService.getNotificationSetting(userId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/global")
    public ResponseEntity<Void> updateGlobalNotification(
            @RequestBody GlobalAlertTypeRequestDTO dto
    ) {
        notificationSettingService.updateGlobalNotification(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/time")
    public ResponseEntity<Void> updateNotificationTime(
            @RequestBody NotificationTimeSettingRequestDTO dto
    ) {
        notificationSettingService.updateNotificationTime(dto);
        return ResponseEntity.ok().build();
    }
}
