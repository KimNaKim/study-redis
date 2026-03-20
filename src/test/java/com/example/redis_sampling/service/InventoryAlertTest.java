package com.example.redis_sampling.service;

import com.example.redis_sampling.domain.Product;
import com.example.redis_sampling.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * 재고 부족 알림 비즈니스 로직 통합 테스트입니다.
 */
@SpringBootTest
class InventoryAlertTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long testProductId;

    @BeforeEach
    void setUp() {
        // 테스트용 상품 저장 (재고 6개로 시작)
        Product product = Product.builder()
                .name("알림 테스트 상품")
                .price(10000.0)
                .stock(6L)
                .description("재고 알림 테스트용 상품입니다.")
                .build();
        Product savedProduct = productRepository.save(product);
        testProductId = savedProduct.getId();
    }

    @Test
    @DisplayName("재고를 5개 미만으로 차감하면 재고 부족 알림이 발행되어야 한다.")
    void testInventoryAlert() throws InterruptedException {
        // Given: 현재 재고 6개 (임계치 5개)

        // When: 2개 차감 -> 남은 재고 4개 (임계치 미만)
        productService.decreaseStock(testProductId, 2L);

        // Then: 
        // 콘솔 로그에서 "⚠️ Inventory Alert Triggered!" 및 
        // "📢 Redis Pub/Sub 메시지 수신" 로그가 출력되는지 확인합니다.
        
        // 비동기 메시지 수신 대기
        TimeUnit.SECONDS.sleep(1);
    }
}
