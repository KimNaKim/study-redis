# [Phase 2] 태스크 1 - 도메인 확장 및 초기 데이터 준비 리포트

Date: 2026-03-19
Reporter: G
Status: 완료

## 📝 작업 요약
Redis Hashes 실습을 위해 상품(Product) 도메인에 '재고(stock)' 필드를 추가하고, 초기 데이터를 구성했습니다.

## 🛠️ 변경 사항
- **엔티티 수정**: `Product.java`에 `Long stock` 필드 추가 및 빌더 생성자 업데이트.
- **DTO 수정**: `ProductDto.java`에 `stock` 필드 추가 및 엔티티 기반 생성자 반영.
- **초기 데이터**: `data.sql`의 모든 INSERT 문에 `stock` 컬럼과 초기값 추가.
- **테스트**: `ProductRepositoryTest.java`에서 `stock` 필드 저장 및 조회를 검증하는 로직 추가.

## ✅ 검증 결과
- `gradlew test` 실행 결과 `ProductRepositoryTest` 통과 (BUILD SUCCESSFUL).
- H2 DB 내 `stock` 필드 저장 및 매핑 정상 확인.

## 💡 비유로 설명하기
"상품 정보라는 상자에 '재고'라는 칸을 하나 더 만들었습니다. 이제 손님에게 상품 이름과 가격뿐만 아니라, 창고에 몇 개가 남았는지도 알려줄 수 있게 되었습니다. 또한, 가게를 처음 열 때(SQL 초기화) 이 칸을 미리 채워두도록 설정했습니다."
