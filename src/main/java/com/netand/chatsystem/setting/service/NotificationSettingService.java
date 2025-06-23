package com.netand.chatsystem.setting.service;

import com.netand.chatsystem.setting.dto.*;
import com.netand.chatsystem.user.entity.User;

public interface NotificationSettingService {
    NotificationSettingResponseDTO updateGlobalNotification(GlobalAlertTypeRequestDTO dto);
    NotificationSettingResponseDTO updateNotificationTime(NotificationTimeSettingRequestDTO dto);
    NotificationSettingResponseDTO updateChatRoomNotification(RoomNotifySetRequestDTO dto);
    NotificationSettingResponseDTO getNotificationSetting(Long userId);
    RoomNotifySetResponseDTO getRoomNotifySetting(Long userId, Long chatRoomId);
}
