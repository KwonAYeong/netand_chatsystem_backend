package com.netand.chatsystem.setting.service;

import com.netand.chatsystem.setting.dto.GlobalAlertTypeRequestDTO;

public interface NotificationSettingService {
    void updateGlobalNotification(GlobalAlertTypeRequestDTO dto);
}
