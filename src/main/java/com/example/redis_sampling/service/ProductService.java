package com.example.redis_sampling.service;

import com.example.redis_sampling.domain.Product;
import com.example.redis_sampling.domain.ProductRepository;
import com.example.redis_sampling.dto.CacheResponse;
import com.example.redis_sampling.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "redis-lab:product:";

    public CacheResponse<ProductDto> getProductWithCaching(Long id) {
        long startTime = System.currentTimeMillis();
        String key = CACHE_KEY_PREFIX + id;

        // 1. Redis 캐시 확인
        ProductDto cachedDto = (ProductDto) redisTemplate.opsForValue().get(key);
        if (cachedDto != null) {
            log.info("Cache Hit for product: {}", id);
            return new CacheResponse<>(cachedDto, "CACHE", System.currentTimeMillis() - startTime);
        }

        // 2. Cache Miss -> DB 조회 (인위적 지연 1.5초 추가)
        log.info("Cache Miss for product: {}. Accessing Database...", id);
        try {
            Thread.sleep(1500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDto dto = new ProductDto(product);

        // 3. Redis에 데이터 캐싱 (TTL 60초)
        redisTemplate.opsForValue().set(key, dto, Duration.ofSeconds(60));

        return new CacheResponse<>(dto, "DATABASE", System.currentTimeMillis() - startTime);
    }
}
