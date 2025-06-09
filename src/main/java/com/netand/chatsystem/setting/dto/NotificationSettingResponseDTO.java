package com.netand.chatsystem.setting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSettingResponseDTO {
    private boolean isMuteAll;
    private LocalTime notificationStartTime;
    private LocalTime notificationEndTime;
}