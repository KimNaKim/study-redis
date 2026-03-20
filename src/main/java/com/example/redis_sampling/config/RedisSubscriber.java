package com.example.redis_sampling.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.example.redis_sampling.controller.SseController;

/**
 * Redis Pub/Sub의 구독자(Subscriber) 역할을 수행하는 클래스입니다.
 * Redis로부터 수신된 메시지를 처리합니다.
 */
@Slf4j
@Service
public class RedisSubscriber implements MessageListener {

    /**
     * Redis 서버로부터 메시지가 도착했을 때 호출되는 콜백 메서드
     * 
     * @param message 메시지 본문
     * @param pattern 구독 채널 패턴 (필요 시)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msg = new String(message.getBody());
        String channel = new String(message.getChannel());
        
        log.info("📢 Redis Pub/Sub 메시지 수신 - 채널: {}, 내용: {}", channel, msg);
        
        // SSE(Server-Sent Events)를 통해 연결된 모든 웹 클라이언트로 브로드캐스트
        SseController.broadcast(msg);
    }
}
