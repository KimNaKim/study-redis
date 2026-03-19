# Report: Hash 필드 단위 부분 업데이트 구현

**Date**: 2026-03-19  
**Reporter**: G  
**Task**: Phase 2 - 태스크 3 (필드 단위 부분 업데이트 기능 구현)

## 📝 작업 요약
Redis Hashes의 핵심 기능인 필드 단위 부분 업데이트(`HSET`)와 원자적 증감 연산(`HINCRBY`)을 구현했습니다. 이를 통해 전체 객체를 다시 직렬화하지 않고도 가격 수정 및 재고 차감 기능을 효율적으로 처리할 수 있게 되었습니다.

## 🛠️ 변경 사항
- **Product Entity**:
    - 비즈니스 로직 처리를 위한 `updatePrice`, `decreaseStock` 메서드 추가.
- **ProductService**:
    - `updateProductPrice`: `opsForHash().put()`을 사용하여 Redis 내 'price' 필드만 수정하고 DB와 동기화.
    - `decreaseStock`: `opsForHash().increment()`를 사용하여 Redis 내 'stock' 필드를 원자적으로 차감하고 DB와 동기화.
    - 데이터 정합성을 위해 업데이트 시 기존 Strings 방식 캐시(`redis-sampling:product:{id}`) 삭제.
- **ProductController**:
    - `PATCH /api/products/{id}/price`: 가격 수정 API 추가.
    - `POST /api/products/{id}/decrease-stock`: 재고 차감 API 추가.
- **Web UI (product-list.mustache)**:
    - 상품별 가격 입력 필드 및 **[Update Price]**, **[Buy]** 버튼 추가.
    - 부분 업데이트 성공 시 실시간으로 로그를 출력하여 동작 원리 시각화.

## ✅ 검증 방법 (사용자 가이드)
1. **가격 수정 테스트**:
    - UI의 'Price' 입력란에 새로운 값을 넣고 **[Update Price]** 클릭.
    - 로그 창에 `Redis [price] field updated via HSET` 메시지가 뜨는지 확인.
    - 우측 상세 정보창의 가격이 즉시 반영되는지 확인.
2. **원자적 재고 차감 테스트**:
    - **[Buy (1)]** 버튼 클릭.
    - 로그 창에 `Atomic stock reduction via HINCRBY` 메시지가 뜨는지 확인.
    - 재고(Stock) 값이 정확히 1씩 줄어드는지 확인.
3. **데이터 정합성 확인**:
    - 업데이트 후 **[Fetch (Hash)]**를 클릭하여 캐시 데이터가 최신화되었는지 확인.

## 💡 비유로 설명하는 부분 업데이트
기존 방식이 서류 전체를 새로 작성해서 교체하는 방식이었다면, 이번 **부분 업데이트**는 서류의 특정 칸(예: 가격 칸)만 지우개로 지우고 새로 쓰는 것과 같습니다. 특히 **재고 차감(HINCRBY)**은 여러 사람이 동시에 수정하려고 할 때, 순서를 정해 한 명씩 정확히 처리해주는 **'번호표를 든 은행 창구'**와 같아서 데이터가 꼬일 걱정이 없습니다.
