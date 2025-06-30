package com.netand.chatsystem.chat.entity;

import com.netand.chatsystem.common.BaseTimeEntity;
import com.netand.chatsystem.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_participant",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"chat_room_id", "user_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_message_id")
    private ChatMessage lastReadMessage;

    private LocalDateTime leftAt;

    private LocalDateTime joinedAt;

    private LocalDateTime updatedAt;

    public void setLastReadMessage(ChatMessage lastReadMessage) { this.lastReadMessage = lastReadMessage;}

    public void leave() { this.leftAt = LocalDateTime.now();}

    public void setLeftAt(LocalDateTime leftAt) { this.leftAt = leftAt;}

    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt;}

    @PrePersist
    protected void onCreate() { this.joinedAt = this.updatedAt = LocalDateTime.now();}

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now();}
}
