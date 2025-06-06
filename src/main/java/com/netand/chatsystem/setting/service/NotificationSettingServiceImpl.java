package com.netand.chatsystem.setting.service;

import com.netand.chatsystem.setting.dto.GlobalAlertTypeRequestDTO;
import com.netand.chatsystem.setting.entity.NotificationSetting;
import com.netand.chatsystem.setting.repository.NotificationSettingRepository;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingServiceImpl implements NotificationSettingService{

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    @Override
    public void updateGlobalNotification(GlobalAlertTypeRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Optional<NotificationSetting> optionalSetting =
                notificationSettingRepository.findByUserIdAndChatRoomIdIsNull(dto.getUserId());

        // 유저의 알람 데이터 유무 분기처리
        if (optionalSetting.isPresent()) {
            NotificationSetting setting = optionalSetting.get();
            setting.updateAlertType(dto.getAlertType());
        } else {
            NotificationSetting setting = NotificationSetting.builder()
                    .user(user)
                    .chatRoom(null)
                    .alertType(dto.getAlertType())
                    .build();
            notificationSettingRepository.save(setting);
        }
    }
}
