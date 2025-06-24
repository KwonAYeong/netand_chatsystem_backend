package com.netand.chatsystem.common.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionManager {
    private final Map<Long, Boolean> activeStatusMap = new ConcurrentHashMap<>();

    public void setOnline(Long userId) {
        activeStatusMap.put(userId, true);
    }

    public void setAway(Long userId) {
        activeStatusMap.put(userId, false);
    }

    public boolean isOnline(Long userId) {
        return activeStatusMap.getOrDefault(userId, false);
    }

    public void remove(Long userId) {
        activeStatusMap.remove(userId);
    }

    public Map<Long, Boolean> getAllStatus() {
        return activeStatusMap;
    }
}
