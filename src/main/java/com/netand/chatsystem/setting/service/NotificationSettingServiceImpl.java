package com.netand.chatsystem.setting.service;

import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.setting.dto.*;
import com.netand.chatsystem.setting.entity.NotificationSetting;
import com.netand.chatsystem.setting.repository.NotificationSettingRepository;
import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
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

        return buildNotificationResponse(userId);
    }

    // 전체 알림 설정
    @Override
    @Transactional
    public NotificationSettingResponseDTO updateGlobalNotification(GlobalAlertTypeRequestDTO dto) {
        NotificationSetting setting = notificationSettingRepository
                .findByUserIdAndChatRoomIdIsNull(dto.getUserId())
                .orElseThrow(() -> new IllegalStateException("알림 설정이 존재하지 않습니다."));

        setting.updateAlertType(dto.getAlertType());

        return buildNotificationResponse(dto.getUserId());
    }


    // 알림 수신 시간 변경
    public NotificationSettingResponseDTO updateNotificationTime(NotificationTimeSettingRequestDTO dto) {
        NotificationSetting setting = notificationSettingRepository
                .findByUserIdAndChatRoomIdIsNull(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 없습니다."));

        setting.updateNotificationTime(dto.getNotificationStartTime(), dto.getNotificationEndTime());

        return buildNotificationResponse(dto.getUserId());
    }

    // 채팅방 알림 설정 정보 가져오기
    public RoomNotifySetResponseDTO getRoomNotifySetting(Long userId, Long chatRoomId) {
        NotificationSetting setting = notificationSettingRepository.findByUserIdAndChatRoomId(userId, chatRoomId)
                .orElseThrow(() -> new IllegalStateException("알림 설정이 존재하지 않습니다."));

        return RoomNotifySetResponseDTO.builder()
                .alertType(setting.getAlertType())
                .build();
    }

    @Override
    public void createNotifySetting(User participantUser, ChatRoom chatRoom) {
        NotificationSetting chatRoomNotifySetting = NotificationSetting.builder()
                .user(participantUser)
                .chatRoom(chatRoom)
                .alertType("ALL")
                .notificationStartTime(LocalTime.of(8, 0))
                .notificationEndTime(LocalTime.of(22, 0))
                .build();
        notificationSettingRepository.save(chatRoomNotifySetting);
    }

    // 채팅방 알림 설정 변경
    public NotificationSettingResponseDTO updateChatRoomNotification(RoomNotifySetRequestDTO dto) {
        NotificationSetting setting = notificationSettingRepository
                .findByUserIdAndChatRoomId(dto.getUserId(), dto.getChatRoomId())
                .orElseThrow(() -> new IllegalStateException("알림 설정이 존재하지 않습니다."));

        setting.updateAlertType(dto.getAlertType());

        return buildNotificationResponse(dto.getUserId());
    }


    //==공통 알림 응답 생성 메서드==//
    private NotificationSettingResponseDTO buildNotificationResponse(Long userId) {
        List<NotificationSetting> settings = notificationSettingRepository.findAllByUserId(userId);

        NotificationSetting globalSetting = settings.stream()
                .filter(s -> s.getChatRoom() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("전체 알림 설정이 존재하지 않습니다."));

        List<Long> receiveMentionOnly = settings.stream()
                .filter(s -> s.getChatRoom() != null && "MENTION_ONLY".equals(s.getAlertType()))
                .map(s -> s.getChatRoom().getId())
                .toList();

        List<Long> mutedChatRoomIds = settings.stream()
                .filter(s -> s.getChatRoom() != null && "NONE".equals(s.getAlertType()))
                .map(s -> s.getChatRoom().getId())
                .toList();

        return NotificationSettingResponseDTO.builder()
                .isMuteAll("NONE".equals(globalSetting.getAlertType()))
                .notificationStartTime(globalSetting.getNotificationStartTime())
                .notificationEndTime(globalSetting.getNotificationEndTime())
                .receiveMentionOnly(receiveMentionOnly)
                .mutedChatRoomIds(mutedChatRoomIds)
                .build();
    }




}
