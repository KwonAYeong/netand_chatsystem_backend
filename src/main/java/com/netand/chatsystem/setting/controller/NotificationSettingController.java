package com.netand.chatsystem.setting.controller;

import com.netand.chatsystem.setting.dto.GlobalAlertTypeRequestDTO;
import com.netand.chatsystem.setting.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification-settings")
@RequiredArgsConstructor
public class NotificationSettingController {
    private final NotificationSettingService notificationSettingService;

    @PutMapping("/global")
    public ResponseEntity<Void> updateGlobalNotification(
            @RequestBody GlobalAlertTypeRequestDTO dto
    ) {
        notificationSettingService.updateGlobalNotification(dto);
        return ResponseEntity.ok().build();
    }
}
