package com.netand.chatsystem.setting.repository;

import com.netand.chatsystem.setting.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUserIdAndChatRoomIdIsNull(Long userId);
    Optional<NotificationSetting> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
    List<NotificationSetting> findAllByUserId(Long userId);
    void deleteByChatRoomId(Long chatRoomId);
}
