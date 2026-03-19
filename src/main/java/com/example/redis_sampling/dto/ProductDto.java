package com.example.redis_sampling.dto;

import com.example.redis_sampling.domain.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

/**
 * 상품 정보를 전달하는 불변 DTO입니다.
 * common-rule.md에 따라 엔티티 주입 생성자를 포함합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For Jackson deserialization
@AllArgsConstructor
@Builder // [Phase 3] 복합 필드 생성을 위해 빌더 추가
@JsonIgnoreProperties(ignoreUnknown = true) // [Phase 3] Redis Hash 매핑 시 없는 필드 무시
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private Long stock;
    
    // [Phase 3] 좋아요 정보 필드 추가
    @Builder.Default
    private Boolean isLiked = false;
    @Builder.Default
    private Long likeCount = 0L;

    // JPA 엔티티를 직접 주입받는 생성자 (불변성 유지)
    public ProductDto(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.description = entity.getDescription();
        this.stock = entity.getStock();
        this.isLiked = false;
        this.likeCount = 0L;
    }

    // 좋아요 정보를 업데이트하여 새 객체 생성 (With 패턴 유사 구현)
    public ProductDto updateLikeInfo(Boolean isLiked, Long likeCount) {
        return ProductDto.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .stock(this.stock)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}
