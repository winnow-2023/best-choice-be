package com.winnow.bestchoice.service;

import com.winnow.bestchoice.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(long userId) {
        SseEmitter emitter = createEmitter(String.valueOf(userId));

        sendToClient(userId, "EventStream Created. [userId=" + userId + "]");
        return emitter;
    }

    private void sendToClient(long id, Object data) {
        SseEmitter emitter = emitterRepository.get(String.valueOf(id));
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(id)).name("sse").data(data));
            } catch (IOException exception) {
                emitterRepository.deleteById(String.valueOf(id));
                emitter.completeWithError(exception);
            }
        }
    }

    private SseEmitter createEmitter(String id) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.save(id, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        return emitter;
    }

    public void notify(long userId, Object event) {
        sendToClient(userId, event);
    }
}
