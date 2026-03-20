package com.example.redis_sampling.service;

import com.example.redis_sampling.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis Pub/Sub의 발행자(Publisher) 역할을 수행하는 서비스 클래스입니다.
 * 특정 토픽(Topic)으로 메시지를 실시간 발행합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 특정 토픽으로 메시지 발행
     * 
     * @param topic 발행할 채널 이름
     * @param message 발행할 메시지 객체 (GenericJackson2JsonRedisSerializer에 의해 JSON 변환됨)
     */
    public void publish(String topic, Object message) {
        log.info("🚀 Redis Pub/Sub 메시지 발행 - 채널: {}, 내용: {}", topic, message);
        redisTemplate.convertAndSend(topic, message);
    }

    /**
     * 재고 알림 전용 발행 메서드 (기본 토픽 사용)
     * 
     * @param message 알림 메시지
     */
    public void publishInventoryAlert(String message) {
        publish(RedisConfig.INVENTORY_ALERT_TOPIC, message);
    }
}
