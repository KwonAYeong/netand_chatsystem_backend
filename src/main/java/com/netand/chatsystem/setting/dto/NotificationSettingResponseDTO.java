package com.netand.chatsystem.setting.dto;

import com.netand.chatsystem.setting.entity.NotificationSetting;
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
    private boolean isMentionOnly;
    private LocalTime notificationStartTime;
    private LocalTime notificationEndTime;

    public static NotificationSettingResponseDTO from(NotificationSetting setting) {
        return NotificationSettingResponseDTO.builder()
                .isMuteAll("NONE".equals(setting.getAlertType()))
                .isMentionOnly("MENTION_ONLY".equals(setting.getAlertType()))
                .notificationStartTime(setting.getNotificationStartTime())
                .notificationEndTime(setting.getNotificationEndTime())
                .build();
    }
}