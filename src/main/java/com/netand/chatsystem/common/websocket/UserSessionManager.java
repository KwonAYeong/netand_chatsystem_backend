package com.netand.chatsystem.common.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionManager {
    private final Map<Long, Boolean> activeStatusMap = new ConcurrentHashMap<>();

    public void setOnline(Long userId)
    {
        activeStatusMap.put(userId, true);
        // 세션 메모리 로그 확인
        System.out.println("UserSessionManager.setOnline");
        System.out.println("세션 메모리 = " + activeStatusMap + ", userId = " + userId);
    }

    public void setAway(Long userId) {
        activeStatusMap.put(userId, false);
        // 세션 메모리 로그 확인
        System.out.println("UserSessionManager.setAway");
        System.out.println("세션 메모리 = " + activeStatusMap + ", userId = " + userId);
    }

    public boolean isOnline(Long userId) {
        System.out.println("UserSessionManager.isOnline");
        System.out.println("세션 메모리 = " + activeStatusMap + ", userId = " + userId);
        return activeStatusMap.getOrDefault(userId, false);
    }

    public void remove(Long userId) {
        activeStatusMap.remove(userId);
    }

    public Map<Long, Boolean> getAllStatus() {
        return activeStatusMap;
    }
}
