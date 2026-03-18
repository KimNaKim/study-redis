# 리포트: 컨트롤러 내 중복 데이터 초기화 로직 제거

**Date**: 2026-03-18  
**Reporter**: KimNaKim  
**Status**: [x] 태스크 완료

## 1. 작업 요약
- `ProductController` 내부에 하드코딩되어 있던 임시 데이터 생성 로직을 제거했습니다.
- 이를 통해 `data.sql` 기반의 데이터 초기화 규칙을 준수하도록 정제했습니다.

## 2. 변경 사항
- **ProductController.java**: 
    - `productListPage` 메서드에서 `if (count == 0)`을 통한 데이터 직접 삽입 로직 삭제.
    - 이제 상품 목록 요청 시 순수하게 DB의 모든 레코드를 조회하여 전달합니다.

## 3. 검증 결과
- `/products` 접속 시 상품명이 `Redis Machine`, `Spring Boot Master`, `Mechanical Keyboard` (설명에 SQL Init 포함)로 정상 출력되는지 확인했습니다.
- **비유로 설명**: 마치 메뉴판을 보러 온 손님에게 주방장이 즉석에서 임시 메뉴를 만들어 내던 방식에서, 정식으로 고정된 정식 메뉴판(data.sql)만 정직하게 보여주도록 규칙을 바로잡은 것과 같습니다.

## 4. 향후 계획
- Phase 2 진행 전, 환경 설정이 완전히 마무리되었으므로 새로운 학습 단계인 Hashes 구현을 위한 TODO 리스트를 작성할 예정입니다.
