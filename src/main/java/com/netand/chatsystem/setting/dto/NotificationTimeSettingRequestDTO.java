package com.netand.chatsystem.setting.dto;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class NotificationTimeSettingRequestDTO {
    private Long userId;
    private LocalTime startTime;
    private LocalTime endTime;
}
