package com.example.redis_sampling.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server-Sent Events (SSE)를 통해 클라이언트에게 실시간 알림을 전달하는 컨트롤러입니다.
 */
@Slf4j
@RestController
public class SseController {

    // 연결된 모든 클라이언트 세션을 저장하는 리스트 (스레드 안전)
    private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * 클라이언트가 SSE 구독을 위해 호출하는 엔드포인트
     * 
     * @return SseEmitter 객체
     */
    @GetMapping(value = "/api/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        // 타임아웃을 1시간으로 설정 (실제 운영 환경에선 조절 필요)
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        
        emitters.add(emitter);
        log.info("New SSE Client subscribed. Total clients: {}", emitters.size());

        // 연결 종료 시 리스트에서 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // 초기 연결 성공 메시지 전송 (더미 데이터)
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to Notification Service"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    /**
     * 모든 구독자에게 메시지를 전송하는 정적 메서드
     * 
     * @param message 전송할 내용
     */
    public static void broadcast(String message) {
        log.info("Broadcasting message to {} clients: {}", emitters.size(), message);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("inventory-alert")
                        .data(message));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
