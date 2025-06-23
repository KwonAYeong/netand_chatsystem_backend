package com.netand.chatsystem.notification.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository{
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    @Override
    public void save(String emitterId, SseEmitter emitter) {
        emitterMap.put(emitterId, emitter);
    }

    @Override
    public void deleteById(String emitterId) {
        emitterMap.remove(emitterId);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(Long userId) {
        String prefix = userId + "_";
        return emitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
