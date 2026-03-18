# 리포트: 데이터 초기화 방식 변경 (Java -> SQL)

**Date**: 2026-03-18  
**Reporter**: KimNaKim  
**Status**: [x] 태스크 완료

## 1. 작업 요약
- 하드코딩된 Java 기반의 데이터 삽입 방식(`DataLoader.java`)을 Spring Boot 표준 방식인 `data.sql` 스크립트 방식으로 전환했습니다.
- 프로젝트 공통 규칙(`common-rule.md`)에 데이터베이스 초기화 규약을 추가했습니다.

## 2. 변경 사항
- **DataLoader.java**: 파일 삭제.
- **data.sql**: 신규 생성 및 초기 데이터(3건) 작성.
- **application.properties**: `spring.jpa.defer-datasource-initialization=true` 설정 추가.
- **common-rule.md**: "6. 데이터베이스 초기화" 섹션 추가.

## 3. 검증 결과
- 애플리케이션 재실행 후 `/products` 페이지에서 SQL로 삽입된 데이터가 정상적으로 렌더링됨을 확인했습니다.
- **비유로 설명**: 마치 매번 가게 주인이 아침마다 수동으로 진열대 물건을 채우던(Java) 방식에서, 정해진 목록 리스트(SQL)만 적어두면 자동으로 로봇이 밤사이에 물건을 채워두는 편리한 시스템으로 바꾼 것과 같습니다.

## 4. 향후 계획
- Phase 2(Hashes) 진행 시에도 필요한 초기 데이터는 `data.sql`에 추가하여 관리할 예정입니다.
