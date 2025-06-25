package com.netand.chatsystem.chat.entity;

import com.netand.chatsystem.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "message_interaction",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"message_id", "user_id", "interaction_type"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // MENTION, REACTION, BOOKMARK
    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    // REACTION일 경우만 사용
    private String emoji;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}
