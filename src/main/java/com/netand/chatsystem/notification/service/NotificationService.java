package com.netand.chatsystem.notification.service;

import com.netand.chatsystem.notification.repository.EmitterRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
    private final EmitterRepositoryImpl emitterRepository;

    // 새로운 Emitter 생성하고 저장소에 저장
    public SseEmitter connect(Long userId, String lastEventId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        String emitterId = generateEmitterId(userId);

        emitterRepository.save(emitterId, emitter);

        // 연결 종료 시 Emitter 정리
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        // 더미 이벤트 전송 (연결 확인용)
        sendToClient(emitter, emitterId, "connected");

        return emitter;
    }

    // 알림 메시지 전송
    public void sendNotification(Long userId, String eventName, Object data) {
        emitterRepository.findAllEmitterStartWithByUserId(userId)
                .forEach((emitterId, emitter) -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .id(emitterId)
                                .name(eventName)
                                .data(data));
                        log.info("🔔 Sending notification to userId = {}", userId);
                    } catch (IOException e) {
                        log.error("❌ Failed to send SSE to userId {}: {}", userId, e.getMessage());
                        emitterRepository.deleteById(emitterId);
                        emitter.completeWithError(e);
                    }
                });
    }


    // 유저별로 emitter 고유 식별자 생성
    private String generateEmitterId(Long userId) {
        return userId + "_" + UUID.randomUUID();
    }

    // 	연결 직후 더미 메시지 보내서 연결 확인
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}
