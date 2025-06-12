package com.netand.chatsystem.setting.service;

import com.netand.chatsystem.setting.dto.GlobalAlertTypeRequestDTO;
import com.netand.chatsystem.setting.dto.NotificationSettingResponseDTO;
import com.netand.chatsystem.setting.dto.NotificationTimeSettingRequestDTO;
import com.netand.chatsystem.setting.entity.NotificationSetting;
import com.netand.chatsystem.setting.repository.NotificationSettingRepository;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingServiceImpl implements NotificationSettingService{

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    // 알림 설정 조회
    @Override
    public NotificationSettingResponseDTO getNotificationSetting(Long userId) {
        NotificationSetting setting = notificationSettingRepository
                .findByUserIdAndChatRoomIdIsNull(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

                    // 알림설정 객체 없을 시 생성
                    NotificationSetting defaultSetting = NotificationSetting.builder()
                            .user(user)
                            .chatRoom(null)
                            .alertType("ALL")
                            .notificationStartTime(LocalTime.of(8, 0))
                            .notificationEndTime(LocalTime.of(22, 0))
                            .build();

                    return notificationSettingRepository.save(defaultSetting);
                });

        return NotificationSettingResponseDTO.builder()
                .isMuteAll("NONE".equals(setting.getAlertType()))
                .notificationStartTime(setting.getNotificationStartTime())
                .notificationEndTime(setting.getNotificationEndTime())
                .build();
    }

    // 전체 알림 설정
    @Override
    @Transactional
    public NotificationSettingResponseDTO updateGlobalNotification(GlobalAlertTypeRequestDTO dto) {
        NotificationSetting setting = notificationSettingRepository
                .findByUserIdAndChatRoomIdIsNull(dto.getUserId())
                .orElseThrow(() -> new IllegalStateException("알림 설정이 존재하지 않습니다."));

        setting.updateAlertType(dto.getAlertType());

        return NotificationSettingResponseDTO.from(setting);
    }


    // 알림 수신 시간 변경
    public NotificationSettingResponseDTO updateNotificationTime(NotificationTimeSettingRequestDTO dto) {
        NotificationSetting setting = notificationSettingRepository
                .findByUserIdAndChatRoomIdIsNull(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 없습니다."));

        setting.updateNotificationTime(dto.getNotificationStartTime(), dto.getNotificationEndTime());

        return NotificationSettingResponseDTO.from(setting);
    }


    // 알림 설정 여부 확인
    @Override
    public boolean isNotificationEnabled(User user, Long chatRoomId, String content) {
        NotificationSetting setting =
                notificationSettingRepository.findByUserIdAndChatRoomIdIsNull(user.getId())
                        .orElse(null);

        if (setting == null || "NONE".equals(setting.getAlertType())) {
            return false;
        }

        // 시간 조건 검사
        LocalTime now = LocalTime.now();
        boolean isInTimeRange =
                !now.isBefore(setting.getNotificationStartTime()) &&
                        !now.isAfter(setting.getNotificationEndTime());

        if (!isInTimeRange) return false;

        // ALL이면 무조건 허용
        if ("ALL".equals(setting.getAlertType())) {
            return true;
        }

        if ("MENTION_ONLY".equals(setting.getAlertType())) {
            return content.contains("@" + user.getName());
        }

        return false;
    }



}
