package com.netand.chatsystem.setting.service;

import com.netand.chatsystem.setting.dto.GlobalAlertTypeRequestDTO;
import com.netand.chatsystem.setting.dto.NotificationSettingResponseDTO;
import com.netand.chatsystem.setting.dto.NotificationTimeSettingRequestDTO;
import com.netand.chatsystem.user.entity.User;

public interface NotificationSettingService {
    NotificationSettingResponseDTO updateGlobalNotification(GlobalAlertTypeRequestDTO dto);
    NotificationSettingResponseDTO updateNotificationTime(NotificationTimeSettingRequestDTO dto);
    boolean isNotificationEnabled(User user, Long chatRoomId, String content);
    NotificationSettingResponseDTO getNotificationSetting(Long userId);
}
