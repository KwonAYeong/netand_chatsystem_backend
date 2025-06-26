package com.netand.chatsystem.common.websocket;


import com.netand.chatsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserStatusController {

    private final UserSessionManager userSessionManager;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 1. 자리비움 상태로 전환 요청
    @MessageMapping("/status/away")  // 클라이언트 → /pub/status/away
    public void handleAwayStatus(SimpMessageHeaderAccessor accessor) {
        Long userId = extractUserId(accessor);

        // userId값 확인
        System.out.println("UserStatusController.handleAwayStatus의 userId = " + userId);
        userSessionManager.setAway(userId);  // 메모리 상태 업데이트

        userRepository.findById(userId).ifPresent(user -> {
            user.updateIsActive(false);  // DB 상태 업데이트
            userRepository.save(user);
        });

        messagingTemplate.convertAndSend("/sub/status/" + userId, "AWAY");
        System.out.println("[사용자 설정] userId=" + userId + " → 상태: AWAY");
    }

    // 2. 온라인 상태로 전환 요청
    @MessageMapping("/status/online")  // 클라이언트 → /pub/status/online
    public void handleOnlineStatus(SimpMessageHeaderAccessor accessor) {
        Long userId = extractUserId(accessor);
        // userId값 확인
        System.out.println("UserStatusController.handleOnlineStatus의 userId = " + userId);
        userSessionManager.setOnline(userId);  // 메모리 상태 업데이트

        userRepository.findById(userId).ifPresent(user -> {
            user.updateIsActive(true);  // DB 상태 업데이트
            userRepository.save(user);
        });

        messagingTemplate.convertAndSend("/sub/status/" + userId, "ONLINE");
        System.out.println("[사용자 설정] userId=" + userId + " → 상태: ONLINE");
    }

    // 공통 userId 추출 로직
    private Long extractUserId(SimpMessageHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("userId");
//        System.out.println("✅ userId 헤더 수신됨: " + header);
        if (header.isEmpty()) throw new IllegalArgumentException("userId 헤더가 없습니다!");
        return Long.parseLong(header);
    }

}
