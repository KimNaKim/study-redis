package com.example.redis_sampling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis 연결 및 직렬화 설정을 담당하는 Configuration 클래스입니다.
 * common-rule.md에 정의된 직렬화 규칙을 준수합니다.
 */
@Configuration
public class RedisConfig {

    /**
     * 재고 부족 알림을 위한 공통 토픽 이름 정의
     */
    public static final String INVENTORY_ALERT_TOPIC = "inventory-alerts";

    /**
     * RedisTemplate 빈 등록
     * - Key: StringRedisSerializer (문자열 키 사용)
     * - Value: GenericJackson2JsonRedisSerializer (객체를 JSON 형태로 저장)
     *
     * @param connectionFactory Spring Boot가 자동으로 생성하여 주입하는 Redis 연결 팩토리
     * @return 설정된 RedisTemplate 객체
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key 직렬화 설정: 가독성을 위해 StringSerializer 사용
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Value 직렬화 설정: 객체를 JSON 형태로 자동 변환하여 저장
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    /**
     * Redis 메시지 리스너 컨테이너 설정
     * Redis 서버로부터 메시지를 수신하기 위한 비동기 메시지 처리 엔진입니다.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 특정 토픽(inventory-alerts)을 구독하도록 설정
        container.addMessageListener(listenerAdapter, new ChannelTopic(INVENTORY_ALERT_TOPIC));
        return container;
    }

    /**
     * 메시지 리스너 어댑터 설정
     * 실제 메시지를 수신했을 때 호출될 핸들러 클래스와 메서드를 연결합니다.
     * 여기서는 RedisSubscriber 클래스의 onMessage 메서드를 호출하도록 설정합니다.
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        // "onMessage"는 리스너 클래스에서 구현할 기본 메서드 이름입니다.
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}
