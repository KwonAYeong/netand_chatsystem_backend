package com.netand.chatsystem.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    void save(String emitterId, SseEmitter emitter);

    void deleteById(String emitterId);

    Map<String, SseEmitter> findAllEmitterStartWithByUserId(Long userId);
}
