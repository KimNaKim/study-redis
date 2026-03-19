package com.example.redis_sampling.service;

import com.example.redis_sampling.domain.Product;
import com.example.redis_sampling.domain.ProductRepository;
import com.example.redis_sampling.dto.CacheResponse;
import com.example.redis_sampling.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
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
     * [Phase 2] Strings vs Hashes 성능 및 구조 벤치마크 (Step 4.1 ~ 4.2)
     */
    public Map<String, Object> runBenchmark(Long id) {
        String stringKey = PRODUCT_CACHE_KEY_PREFIX + id;
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        // 사전 준비: 두 캐시에 데이터가 존재하도록 강제 생성
        getProductWithCaching(id);
        getProductFromHash(id);

        Map<String, Object> result = new HashMap<>();
        int iterations = 1000;

        // 1. Strings 방식 벤치마크 (1,000회 반복: GET -> New DTO -> SET)
        long stringStart = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            ProductDto cached = (ProductDto) redisTemplate.opsForValue().get(stringKey);
            if (cached != null) {
                // 불변 DTO이므로 새로 생성 (가장 흔한 패턴)
                ProductDto updated = new ProductDto(cached.getId(), cached.getName(), 
                                                 1000.0 + i, cached.getDescription(), cached.getStock());
                redisTemplate.opsForValue().set(stringKey, updated, Duration.ofSeconds(60));
            }
        }
        long stringEnd = System.currentTimeMillis();
        result.put("stringsTimeMillis", stringEnd - stringStart);

        // 2. Hashes 방식 벤치마크 (1,000회 반복: HSET price)
        long hashStart = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            redisTemplate.opsForHash().put(hashKey, "price", 1000.0 + i);
        }
        long hashEnd = System.currentTimeMillis();
        result.put("hashesTimeMillis", hashEnd - hashStart);

        // 3. 메모리 점유 측정 (Redis MEMORY USAGE 명령어 활용)
        Long stringMemory = redisTemplate.execute((RedisCallback<Long>) connection -> 
            connection.keyCommands().memoryUsage(stringKey.getBytes()));
        Long hashMemory = redisTemplate.execute((RedisCallback<Long>) connection -> 
            connection.keyCommands().memoryUsage(hashKey.getBytes()));

        result.put("stringsMemoryBytes", stringMemory);
        result.put("hashesMemoryBytes", hashMemory);
        result.put("iterations", iterations);

        return result;
    }

    /**
     * [Phase 2] Hashes 기반 캐싱 조회 (Look-aside)
     */
    @Transactional(readOnly = true)
    public CacheResponse<ProductDto> getProductFromHash(Long id) {
        long startTime = System.currentTimeMillis();
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);

        if (!entries.isEmpty()) {
            log.info("Cache Hit (Hashes) - Product ID: {}", id);
            ProductDto cachedProduct = objectMapper.convertValue(entries, ProductDto.class);
            return new CacheResponse<>(cachedProduct, "CACHE (HASH)", System.currentTimeMillis() - startTime);
        }

        log.info("Cache Miss (Hashes) - Fetching from DB: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductDto dto = new ProductDto(product);
        saveProductAsHash(dto);

        return new CacheResponse<>(dto, "DATABASE", System.currentTimeMillis() - startTime);
    }

    /**
     * [Phase 2] 가격 부분 업데이트 (HSET)
     */
    @Transactional
    public void updateProductPrice(Long id, Long newPrice) {
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashKey))) {
            log.info("Updating Redis Hash Field [price] - Key: {}, New Price: {}", hashKey, newPrice);
            redisTemplate.opsForHash().put(hashKey, "price", newPrice.doubleValue());
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.updatePrice(newPrice);
        
        redisTemplate.delete(PRODUCT_CACHE_KEY_PREFIX + id);
    }

    /**
     * [Phase 2] 원자적 재고 차감 (HINCRBY)
     */
    @Transactional
    public void decreaseStock(Long id, Long quantity) {
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashKey))) {
            log.info("Decreasing Redis Hash Field [stock] - Key: {}, Quantity: {}", hashKey, quantity);
            redisTemplate.opsForHash().increment(hashKey, "stock", -quantity);
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.decreaseStock(quantity);

        redisTemplate.delete(PRODUCT_CACHE_KEY_PREFIX + id);
    }

    /**
     * Hashes 구조로 저장하는 공통 로직
     */
    private void saveProductAsHash(ProductDto dto) {
        String hashKey = PRODUCT_HASH_KEY_PREFIX + dto.getId();
        Map<String, Object> productMap = objectMapper.convertValue(dto, Map.class);
        
        log.info("Saving Product as Hash - Key: {}", hashKey);
        redisTemplate.opsForHash().putAll(hashKey, productMap);
        redisTemplate.expire(hashKey, Duration.ofSeconds(60));
    }

    /**
     * [Phase 1] Strings 기반 캐싱 조회 (기존 유지)
     */
    @Transactional(readOnly = true)
    public CacheResponse<ProductDto> getProductWithCaching(Long id) {
        long startTime = System.currentTimeMillis();
        String cacheKey = PRODUCT_CACHE_KEY_PREFIX + id;

        ProductDto cachedProduct = (ProductDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            log.info("Cache Hit (Strings) - Product ID: {}", id);
            return new CacheResponse<>(cachedProduct, "CACHE", System.currentTimeMillis() - startTime);
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductDto dto = new ProductDto(product);
        redisTemplate.opsForValue().set(cacheKey, dto, Duration.ofSeconds(60));

        return new CacheResponse<>(dto, "DATABASE", System.currentTimeMillis() - startTime);
    }
}
