package com.example.redis_sampling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 연결 및 직렬화 설정을 담당하는 Configuration 클래스입니다.
 * common-rule.md에 정의된 직렬화 규칙을 준수합니다.
 */
@Configuration
public class RedisConfig {

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
}
