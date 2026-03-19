# Report: Hash 기반 캐싱 서비스 구현 및 UI 통합

**Date**: 2026-03-19  
**Reporter**: G  
**Task**: Phase 2 - 태스크 2 (Hash 전용 캐싱 서비스 구현)

## 📝 작업 요약
Redis의 `Hashes` 데이터 구조를 활용하여 상품 정보를 필드 단위로 저장하고 조회하는 기능을 구현하고, 이를 사용자가 직접 UI에서 비교 테스트할 수 있도록 통합했습니다.

## 🛠️ 변경 사항
- **ProductService**:
    - `ObjectMapper`를 활용한 DTO <-> Map 변환 로직 추가.
    - `getProductFromHash`: Redis Hash(`HGETALL`) 조회 및 Cache-Aside 패턴 적용.
    - `saveProductAsHash`: Redis Hash(`HSET`) 저장 및 60초 TTL 설정.
- **ProductController**:
    - Hashes 방식 조회를 위한 `/api/products/hash/{id}` 엔드포인트 추가.
- **Web UI (product-list.mustache)**:
    - **Strings** vs **Hashes** 방식을 선택하여 호출할 수 있는 버튼 그룹 추가.
    - 결과 로그에 어떤 구조(`string` 또는 `hash`)로 데이터가 처리되었는지 시각화.

## ✅ 검증 방법 (사용자 가이드)
1. **애플리케이션 실행**: `./gradlew bootRun` 또는 IDE에서 실행.
2. **UI 접속**: `http://localhost:8080/products` 접속.
3. **Hashes 테스트**:
    - 특정 상품의 **[Hashes]** 버튼 클릭 -> 최초 클릭 시 `CACHE MISS` 발생 (DB 조회).
    - 동일 버튼 재클릭 -> `CACHE (HASH) HIT` 발생 (Redis Hash 구조에서 즉시 반환).
4. **구조 확인**:
    - Redis CLI에서 `TYPE redis-sampling:product:hash:1` 실행 시 `hash` 타입임을 확인 가능.

## 💡 비유로 설명하는 Hash 캐싱
기존 **Strings** 방식이 상품 정보를 하나의 커다란 **'밀봉된 박스(JSON 문자열)'**에 통째로 담아 보관하는 것이라면, **Hashes** 방식은 상품의 이름, 가격, 재고 등을 각각의 **'작은 서랍(Field)'**이 있는 **'수납장(Hash)'**에 나누어 보관하는 것과 같습니다. 이렇게 하면 나중에 수납장 전체를 바꾸지 않고도 특정 서랍(예: 가격)만 열어서 내용을 수정할 수 있어 매우 효율적입니다.
