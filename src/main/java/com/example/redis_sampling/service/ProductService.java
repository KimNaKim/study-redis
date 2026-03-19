package com.example.redis_sampling.service;

import com.example.redis_sampling.domain.Product;
import com.example.redis_sampling.domain.ProductRepository;
import com.example.redis_sampling.dto.CacheResponse;
import com.example.redis_sampling.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PRODUCT_CACHE_KEY_PREFIX = "redis-sampling:product:";
    private static final String PRODUCT_HASH_KEY_PREFIX = "redis-sampling:product:hash:";

    /**
     * [Phase 1] Strings 기반 캐싱 조회 (Look-aside)
     */
    @Transactional(readOnly = true)
    public CacheResponse<ProductDto> getProductWithCaching(Long id) {
        long startTime = System.currentTimeMillis();
        String cacheKey = PRODUCT_CACHE_KEY_PREFIX + id;

        // 1. Redis에서 데이터 확인 (Strings)
        ProductDto cachedProduct = (ProductDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            log.info("Cache Hit (Strings) - Product ID: {}", id);
            return new CacheResponse<>(cachedProduct, "CACHE", System.currentTimeMillis() - startTime);
        }

        // 2. 캐시에 없으면 DB 조회
        log.info("Cache Miss (Strings) - Fetching from DB: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductDto dto = new ProductDto(product);

        // 3. Redis에 저장 (TTL 60초)
        redisTemplate.opsForValue().set(cacheKey, dto, Duration.ofSeconds(60));

        return new CacheResponse<>(dto, "DATABASE", System.currentTimeMillis() - startTime);
    }

    /**
     * [Phase 2] Hashes 기반 캐싱 조회 (Look-aside)
     */
    @Transactional(readOnly = true)
    public CacheResponse<ProductDto> getProductFromHash(Long id) {
        long startTime = System.currentTimeMillis();
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        // 1. Redis에서 데이터 확인 (Hashes - HGETALL)
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);

        if (!entries.isEmpty()) {
            log.info("Cache Hit (Hashes) - Product ID: {}", id);
            // Map을 ProductDto로 변환 (Jackson 활용)
            ProductDto cachedProduct = objectMapper.convertValue(entries, ProductDto.class);
            return new CacheResponse<>(cachedProduct, "CACHE (HASH)", System.currentTimeMillis() - startTime);
        }

        // 2. 캐시에 없으면 DB 조회
        log.info("Cache Miss (Hashes) - Fetching from DB: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductDto dto = new ProductDto(product);

        // 3. Redis에 Hash 형태로 저장 (HSET)
        saveProductAsHash(dto);

        return new CacheResponse<>(dto, "DATABASE", System.currentTimeMillis() - startTime);
    }

    /**
     * Hashes 구조로 저장하는 공통 로직
     */
    private void saveProductAsHash(ProductDto dto) {
        String hashKey = PRODUCT_HASH_KEY_PREFIX + dto.getId();
        // ProductDto -> Map 변환
        Map<String, Object> productMap = objectMapper.convertValue(dto, Map.class);
        
        log.info("Saving Product as Hash - Key: {}", hashKey);
        redisTemplate.opsForHash().putAll(hashKey, productMap);
        redisTemplate.expire(hashKey, Duration.ofSeconds(60));
    }
}
