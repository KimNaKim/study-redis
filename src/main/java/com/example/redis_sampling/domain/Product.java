package com.example.redis_sampling.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 정보를 담는 JPA 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double price;

    private String description;

    private Long stock;

    @Builder
    public Product(String name, Double price, String description, Long stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    /**
     * 가격 업데이트
     */
    public void updatePrice(Long newPrice) {
        this.price = newPrice.doubleValue();
    }

    /**
     * 재고 차감
     */
    public void decreaseStock(Long quantity) {
        if (this.stock < quantity) {
            throw new RuntimeException("Not enough stock");
        }
        this.stock -= quantity;
    }
}
