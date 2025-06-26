package com.netand.chatsystem.common.websocket;

import com.netand.chatsystem.user.entity.User;
import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
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
    private final ObjectProvider<SimpMessagingTemplate> messagingTemplateProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessagingTemplate messagingTemplate = messagingTemplateProvider.getIfAvailable();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        StompCommand command = accessor.getCommand();

        try {
            if (StompCommand.CONNECT.equals(command)) {
                Long userId = extractUserId(accessor);
                if (userId == null) {
                    System.err.println("❌ userId is null — CONNECT 거부");
                    return null;
                }

                accessor.getSessionAttributes().put("userId", userId);

                boolean isSettingOnline = userRepository.findById(userId)
                        .map(User::isActive)
                        .orElse(false);

                if (isSettingOnline) {
                    sessionManager.setOnline(userId);
                } else {
                    sessionManager.setAway(userId);
                }

                if (messagingTemplate != null) {
                    String status = isSettingOnline ? "ONLINE" : "AWAY";
                    messagingTemplate.convertAndSend("/sub/status/" + userId, status);
                    System.out.println("[CONNECT] userId=" + userId + " → broadcast: " + status);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ preSend 예외: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return message; // ✅ 반드시 메시지 반환
    }




    private Long extractUserId(StompHeaderAccessor accessor) {
        return Long.parseLong(accessor.getFirstNativeHeader("userId"));
    }
}


