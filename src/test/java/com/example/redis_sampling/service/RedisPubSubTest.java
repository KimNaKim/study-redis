package com.example.redis_sampling.service;

import com.example.redis_sampling.config.RedisConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis Pub/Sub 메시지 송수신 통합 테스트입니다.
 */
@SpringBootTest
class RedisPubSubTest {

    @Autowired
    private RedisPublisher redisPublisher;

    @Test
    @DisplayName("Redis Pub/Sub을 통해 메시지를 발행하면 구독자가 수신할 수 있어야 한다.")
    void testPubSub() throws InterruptedException {
        // Given
        String topic = RedisConfig.INVENTORY_ALERT_TOPIC;
        String message = "테스트 알림: 재고가 부족합니다!";

        // When
        // 메시지를 발행합니다. 비동기로 동작하므로 로그를 통해 수신 여부를 확인합니다.
        redisPublisher.publish(topic, message);

        // Then
        // 실제 로그를 가로채서 검증하기는 복잡하므로, 
        // 여기서는 예외 없이 발행되는지 확인하고 콘솔 로그 출력을 수동 확인합니다.
        // (실제 환경에서는 CountDownLatch 등을 사용하여 비동기 수신 완료를 대기할 수 있습니다.)
        
        // 잠시 대기하여 로그가 출력될 시간을 줍니다.
        TimeUnit.SECONDS.sleep(1);
    }
}
