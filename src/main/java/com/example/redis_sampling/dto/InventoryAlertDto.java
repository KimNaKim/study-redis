package com.example.redis_sampling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 재고 부족 알림 정보를 담는 DTO 클래스입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertDto {
    private Long productId;
    private String productName;
    private Long currentStock;
    private String message;
    private LocalDateTime timestamp;

    /**
     * 기본 알림 메시지 생성 정적 팩토리 메서드
     */
    public static InventoryAlertDto of(Long productId, String productName, Long currentStock) {
        return InventoryAlertDto.builder()
                .productId(productId)
                .productName(productName)
                .currentStock(currentStock)
                .message(String.format("⚠️ [재고 부족] 상품 '%s'(ID: %d)의 재고가 %d개 남았습니다!", productName, productId, currentStock))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
