# 리포트: JPA 도메인 및 DTO 구현

**Date**: 2026-03-18  
**Reporter**: KimNaKim  
**Status**: [x] 태스크 4 완료

## 1. 작업 요약
- 캐싱할 데이터의 근간이 되는 `Product` 엔티티와 이를 처리할 Repository를 구축했습니다.
- 계층 간 데이터 전달을 위해 불변 객체인 `ProductDto`를 설계했습니다.

## 2. 변경 사항
- **Product.java**: JPA 엔티티 정의 (Lombok Builder 패턴 적용).
- **ProductRepository.java**: Spring Data JPA 인터페이스 생성.
- **ProductDto.java**: 
    - 불변 객체 설계 (`final` 필드, `@Getter`).
    - 엔티티를 주입받아 직접 변환하는 생성자 구현 (`common-rule.md` 준수).
- **ProductRepositoryTest.java**: H2 DB를 활용한 영속성 계층 검증 테스트 추가.

## 3. 검증 결과
- `./gradlew test --tests ...ProductRepositoryTest` 실행 결과 **BUILD SUCCESSFUL** 확인.
- **비유로 설명**: 마치 물류 창고(DB)에 물건을 담을 규격화된 상자(Entity)를 만들고, 손님에게 배송할 때 사용할 튼튼한 포장 박스(DTO)를 준비한 것과 같습니다.

## 4. 향후 계획
- **태스크 5**: Redis를 연동하여 실제 캐싱 로직(DB 조회 전 캐시 확인 및 TTL 적용)을 구현하는 `ProductService`를 작성합니다.
