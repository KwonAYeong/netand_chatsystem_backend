package com.netand.chatsystem.common.websocket;

import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class WebSocketConnectionInterceptor implements ChannelInterceptor {

    private final UserSessionManager sessionManager;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            if (StompCommand.CONNECT.equals(command)) {
                Long userId = extractUserId(accessor);
                accessor.getSessionAttributes().put("userId", userId); // ⭐ 저장

                sessionManager.setOnline(userId);

                boolean isSettingOnline = userRepository.findById(userId)
                        .map(User::isActive)
                        .orElse(false);

                String status = isSettingOnline ? "ONLINE" : "AWAY";
                messagingTemplate.convertAndSend("/sub/status/" + userId, status);

                System.out.println("[CONNECT] userId=" + userId + " → broadcast: " + status);
            }

            else if (StompCommand.DISCONNECT.equals(command)) {
                Long userId = (Long) accessor.getSessionAttributes().get("userId");

                if (userId != null) {
                    sessionManager.setAway(userId);
                    messagingTemplate.convertAndSend("/sub/status/" + userId, "AWAY");

                    System.out.println("[DISCONNECT] userId=" + userId + " → broadcast: AWAY");
                } else {
                    System.out.println("[DISCONNECT] userId not found in session attributes");
                }
            }
        }

        return message;
    }


    private Long extractUserId(StompHeaderAccessor accessor) {
        return Long.parseLong(accessor.getFirstNativeHeader("userId"));
    }
}


