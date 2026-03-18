package com.example.redis_sampling.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 정보가 정상적으로 저장되고 조회되어야 한다.")
    void saveAndFindProductTest() {
        // Given
        Product product = Product.builder()
                .name("Redis Machine")
                .price(10000.0)
                .description("Fastest caching machine")
                .build();

        // When
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElseThrow();

        // Then
        assertThat(foundProduct.getName()).isEqualTo("Redis Machine");
        assertThat(foundProduct.getPrice()).isEqualTo(10000.0);
    }
}
