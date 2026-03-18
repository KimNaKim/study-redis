package com.example.redis_sampling.dto;

import com.example.redis_sampling.domain.Product;
import lombok.Getter;

/**
 * 상품 정보를 전달하는 불변 DTO입니다.
 * common-rule.md에 따라 엔티티 주입 생성자를 포함합니다.
 */
@Getter
public class ProductDto {
    private final Long id;
    private final String name;
    private final Double price;
    private final String description;

    // JPA 엔티티를 직접 주입받는 생성자 (불변성 유지)
    public ProductDto(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.description = entity.getDescription();
    }
}
