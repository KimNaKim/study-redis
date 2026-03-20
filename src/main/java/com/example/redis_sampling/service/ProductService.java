package com.example.redis_sampling.service;

import com.example.redis_sampling.domain.Product;
import com.example.redis_sampling.domain.ProductRepository;
import com.example.redis_sampling.dto.CacheResponse;
import com.example.redis_sampling.dto.InventoryAlertDto;
import com.example.redis_sampling.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisPublisher redisPublisher; // Redis Publisher 주입

    private static final String PRODUCT_CACHE_KEY_PREFIX = "redis-sampling:product:";
    private static final String PRODUCT_HASH_KEY_PREFIX = "redis-sampling:product:hash:";
    private static final String RECENT_PRODUCTS_KEY = "redis-sampling:user:guest:recent-products";
    private static final String PRODUCT_LIKES_KEY_PREFIX = "redis-sampling:product:likes:";
    private static final String DAILY_VISITORS_KEY_PREFIX = "redis-sampling:uv:";
    private static final String RANKING_KEY = "redis-sampling:ranking";

    private static final long STOCK_THRESHOLD = 5; // 재고 알림 임계치

    /**
     * [Phase 4] 상품 조회수 증가 (ZINCRBY)
     */
    public void incrementViewCount(Long productId) {
        log.info("Incrementing View Count (ZSet) - Product ID: {}", productId);
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, productId.toString(), 1);
    }

    /**
     * [Phase 4] 실시간 인기 랭킹 조회 (ZREVRANGE)
     */
    public List<Map<String, Object>> getTopRankedProducts(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> rankedItems = 
                redisTemplate.opsForZSet().reverseRangeWithScores(RANKING_KEY, 0, limit - 1);

        if (rankedItems == null || rankedItems.isEmpty()) {
            return Collections.emptyList();
        }

        return rankedItems.stream()
                .map(tuple -> {
                    Map<String, Object> map = new HashMap<>();
                    Long productId = Long.parseLong(tuple.getValue().toString());
                    Double score = tuple.getScore();

                    // 상품 상세 정보 조회 (캐시 활용)
                    String hashKey = PRODUCT_HASH_KEY_PREFIX + productId;
                    Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);
                    ProductDto dto;
                    if (!entries.isEmpty()) {
                        dto = objectMapper.convertValue(entries, ProductDto.class);
                    } else {
                        Product product = productRepository.findById(productId).orElse(null);
                        dto = product != null ? new ProductDto(product) : null;
                    }

                    if (dto != null) {
                        // 좋아요 정보 추가
                        Map<String, Object> likeInfo = getLikeInfo(productId, "guest");
                        dto = dto.updateLikeInfo((Boolean) likeInfo.get("isLiked"), (Long) likeInfo.get("likeCount"));
                    }

                    map.put("product", dto);
                    map.put("score", score != null ? score.longValue() : 0L);
                    return map;
                })
                .filter(m -> m.get("product") != null)
                .collect(Collectors.toList());
    }

    /**
     * [Phase 3] 좋아요 토글 (SADD / SREM)
     */
    public void toggleLike(Long productId, String userId) {
        String key = PRODUCT_LIKES_KEY_PREFIX + productId;
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId);

        if (Boolean.TRUE.equals(isMember)) {
            redisTemplate.opsForSet().remove(key, userId);
            log.info("Removed Like - Product: {}, User: {}", productId, userId);
        } else {
            redisTemplate.opsForSet().add(key, userId);
            log.info("Added Like - Product: {}, User: {}", productId, userId);
        }
    }

    /**
     * [Phase 3] 좋아요 정보 조회 (SISMEMBER, SCARD)
     */
    public Map<String, Object> getLikeInfo(Long productId, String userId) {
        String key = PRODUCT_LIKES_KEY_PREFIX + productId;
        Map<String, Object> info = new HashMap<>();
        info.put("isLiked", Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId)));
        info.put("likeCount", Objects.requireNonNullElse(redisTemplate.opsForSet().size(key), 0L));
        return info;
    }

    /**
     * [Phase 3] 유니크 방문자 집계 (SADD)
     */
    public void trackVisitor(String userId) {
        String today = java.time.LocalDate.now().toString();
        String key = DAILY_VISITORS_KEY_PREFIX + today;

        redisTemplate.opsForSet().add(key, userId);
        redisTemplate.expire(key, Duration.ofDays(1)); // 1일 후 만료
    }

    /**
     * [Phase 3] 오늘의 유니크 방문자 수 조회 (SCARD)
     */
    public Long getTodayVisitorCount() {
        String today = java.time.LocalDate.now().toString();
        String key = DAILY_VISITORS_KEY_PREFIX + today;
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * [Phase 3] 최근 본 상품 추가 (LPUSH + LTRIM)
     */
    @Transactional(readOnly = true)
    public CacheResponse<ProductDto> getProductFromHash(Long id) {
        long startTime = System.currentTimeMillis();
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        // [Phase 3] 최근 본 상품 추가
        addToRecentProducts(id);
        
        // [Phase 4] 실시간 랭킹 점수(조회수) 증가
        incrementViewCount(id);

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
     * [Phase 5] 재고 부족 시 실시간 알림 발행 (Pub/Sub)
     */
    @Transactional
    public void decreaseStock(Long id, Long quantity) {
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        // 1. Redis Hash 기반 원자적 차감 및 결과값 확인
        Long remainingStock = null;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashKey))) {
            log.info("Decreasing Redis Hash Field [stock] - Key: {}, Quantity: {}", hashKey, quantity);
            remainingStock = redisTemplate.opsForHash().increment(hashKey, "stock", -quantity);
        }

        // 2. DB 동기화
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.decreaseStock(quantity);
        
        // Redis 캐시가 없었거나 결과가 null인 경우 DB 값 사용
        if (remainingStock == null) {
            remainingStock = product.getStock();
        }

        // 3. [Phase 5] 재고 부족 알림 발행 트리거 (임계치 미만인 경우)
        if (remainingStock < STOCK_THRESHOLD) {
            log.warn("⚠️ Inventory Alert Triggered! - Product: {}, Stock: {}", product.getName(), remainingStock);
            InventoryAlertDto alertDto = InventoryAlertDto.of(id, product.getName(), remainingStock);
            redisPublisher.publishInventoryAlert(alertDto.getMessage());
        }

        redisTemplate.delete(PRODUCT_CACHE_KEY_PREFIX + id);
    }

    /**
     * [Phase 2] 캐시 수동 삭제 (Step 5.2)
     */
    public void clearCache(Long id, String type) {
        String key = type.equals("hash") ? PRODUCT_HASH_KEY_PREFIX + id : PRODUCT_CACHE_KEY_PREFIX + id;
        log.info("Clearing Cache - Type: {}, Key: {}", type, key);
        redisTemplate.delete(key);
    }

    /**
     * [Phase 2] 성능 벤치마크 (Step 4.1 ~ 4.2)
     */
    public Map<String, Object> runBenchmark(Long id) {
        String stringKey = PRODUCT_CACHE_KEY_PREFIX + id;
        String hashKey = PRODUCT_HASH_KEY_PREFIX + id;

        getProductWithCaching(id);
        getProductFromHash(id);

        Map<String, Object> result = new HashMap<>();
        int iterations = 1000;

        long stringStart = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            ProductDto cached = (ProductDto) redisTemplate.opsForValue().get(stringKey);
            if (cached != null) {
                ProductDto updated = ProductDto.builder()
                        .id(cached.getId())
                        .name(cached.getName())
                        .price(1000.0 + i)
                        .description(cached.getDescription())
                        .stock(cached.getStock())
                        .build();
                redisTemplate.opsForValue().set(stringKey, updated, Duration.ofSeconds(60));
            }
        }
        long stringEnd = System.currentTimeMillis();
        result.put("stringsTimeMillis", stringEnd - stringStart);

        long hashStart = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            redisTemplate.opsForHash().put(hashKey, "price", 1000.0 + i);
        }
        long hashEnd = System.currentTimeMillis();
        result.put("hashesTimeMillis", hashEnd - hashStart);

        Long stringMemory = 0L;
        Long hashMemory = 0L;
        try {
            stringMemory = redisTemplate.execute((RedisCallback<Long>) connection -> 
                (Long) connection.execute("MEMORY", "USAGE".getBytes(), stringKey.getBytes()));
            hashMemory = redisTemplate.execute((RedisCallback<Long>) connection -> 
                (Long) connection.execute("MEMORY", "USAGE".getBytes(), hashKey.getBytes()));
        } catch (Exception e) {
            log.warn("MEMORY USAGE failed: {}", e.getMessage());
        }

        result.put("stringsMemoryBytes", stringMemory != null ? stringMemory : 0L);
        result.put("hashesMemoryBytes", hashMemory != null ? hashMemory : 0L);
        result.put("iterations", iterations);

        return result;
    }

    /**
     * [Phase 3] 최근 본 상품 추가 (LPUSH + LTRIM)
     */
    public void addToRecentProducts(Long productId) {
        String key = RECENT_PRODUCTS_KEY;
        // 1. 기존 리스트에서 해당 상품 ID 삭제 (중복 방지 및 최신순 정렬을 위함)
        redisTemplate.opsForList().remove(key, 0, productId.toString());
        // 2. 리스트의 가장 앞에 추가
        redisTemplate.opsForList().leftPush(key, productId.toString());
        // 3. 최근 5개만 유지 (0 ~ 4)
        redisTemplate.opsForList().trim(key, 0, 4);
        log.info("Added to Recent Products - ID: {}", productId);
    }

    /**
     * [Phase 3] 최근 본 상품 목록 조회 (LRANGE)
     */
    public List<ProductDto> getRecentProducts() {
        List<Object> productIds = redisTemplate.opsForList().range(RECENT_PRODUCTS_KEY, 0, -1);
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ID 목록을 바탕으로 실제 상품 정보 조회
        return productIds.stream()
                .map(id -> {
                    try {
                        Long productId = Long.parseLong(id.toString());
                        String hashKey = PRODUCT_HASH_KEY_PREFIX + productId;
                        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);
                        
                        ProductDto dto;
                        if (!entries.isEmpty()) {
                            dto = objectMapper.convertValue(entries, ProductDto.class);
                        } else {
                            Product product = productRepository.findById(productId).orElse(null);
                            dto = product != null ? new ProductDto(product) : null;
                        }

                        if (dto != null) {
                            // 좋아요 정보 추가
                            Map<String, Object> likeInfo = getLikeInfo(productId, "guest");
                            return dto.updateLikeInfo((Boolean) likeInfo.get("isLiked"), (Long) likeInfo.get("likeCount"));
                        }
                        return null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Hashes 구조로 저장하는 공통 로직
     */
    private void saveProductAsHash(ProductDto dto) {
        String hashKey = PRODUCT_HASH_KEY_PREFIX + dto.getId();
        Map<String, Object> productMap = objectMapper.convertValue(dto, Map.class);
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
