package com.netand.chatsystem.setting.entity;

import com.netand.chatsystem.chat.entity.ChatRoom;
import com.netand.chatsystem.common.BaseTimeEntity;
import com.netand.chatsystem.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "notification_setting", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "chat_room_id"})
})
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
@Builder
public class NotificationSetting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_setting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    /**
       알림 유형들:
       'ALL', 'MENTION_ONLY', 'NONE'
     */
    @Column(name = "alert_type", nullable = false)
    private String alertType;

    @Builder.Default
    @Column(name = "notification_start_time")
    private LocalTime notificationStartTime = LocalTime.of(8,0);

    @Builder.Default
    @Column(name = "notification_end_time")
    private LocalTime notificationEndTime = LocalTime.of(22,0);

    //==수신 유형 변경==//
    public void updateAlertType(String alertType) {
        this.alertType = alertType;
    }

    //==알림 수신 시간 변경==//
    public void updateNotificationTime(LocalTime notificationStartTime, LocalTime notificationEndTime) {
        this.notificationStartTime = notificationStartTime;
        this.notificationEndTime = notificationEndTime;
    }

}
