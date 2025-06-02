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
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "alert_type", nullable = false)
    private String alertType;

    @Column(name = "notification_start_time")
    private LocalTime notificationStartTime;

    @Column(name = "notification_end_time")
    private LocalTime notificationEndTime;

}
