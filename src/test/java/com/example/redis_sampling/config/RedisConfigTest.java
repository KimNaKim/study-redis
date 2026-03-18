package com.example.redis_sampling.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("RedisTemplate 빈이 정상적으로 주입되어야 한다.")
    void redisTemplateBeanLoadingTest() {
        // Given & When & Then
        assertThat(redisTemplate).isNotNull();
        assertThat(redisTemplate.getKeySerializer()).isNotNull();
        assertThat(redisTemplate.getValueSerializer()).isNotNull();
    }
}
