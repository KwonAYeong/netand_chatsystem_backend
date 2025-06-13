package com.netand.chatsystem.setting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.netand.chatsystem.setting.entity.NotificationSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSettingResponseDTO {
    private boolean isMuteAll;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm") // "09:00" 형식으로 포맷팅해줌.
    private LocalTime notificationStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime notificationEndTime;

    private List<Long> receiveMentionOnly;
    private List<Long> mutedChatRoomIds;


}