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

        // 1. Redis Hash 필드만 수정 (HSET)
        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashKey))) {
            log.info("Updating Redis Hash Field [price] - Key: {}, New Price: {}", hashKey, newPrice);
            redisTemplate.opsForHash().put(hashKey, "price", newPrice);
        }

        // 2. DB 업데이트 (동기화)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.updatePrice(newPrice);
        
        // 3. 기존 Strings 캐시 삭제 (데이터 정합성 유지)
        redisTemplate.delete(PRODUCT_CACHE_KEY_PREFIX + id);
    }

    /**
     * [Phase 2] 원자적 재고 차감 (HINCRBY)
     */
    @Transactional
    public void decreaseStock(Long id, Long quantity) {
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        // 1. Redis Hash 필드 원자적 차감 (HINCRBY)
        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashKey))) {
            log.info("Decreasing Redis Hash Field [stock] - Key: {}, Quantity: {}", hashKey, quantity);
            // increment에 음수를 전달하여 차감 수행
            redisTemplate.opsForHash().increment(hashKey, "stock", -quantity);
        }

        // 2. DB 업데이트 (동기화)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.decreaseStock(quantity);

        // 3. 기존 Strings 캐시 삭제 (데이터 정합성 유지)
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
