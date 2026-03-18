# Reporter: KimNaKim
# 날짜: 2026-03-18
# 작업 단계: Phase 1 - 태스크 1 (환경 설정)

## 작업 요약
Redis 학습을 위한 프로젝트의 기초 뼈대를 잡는 작업을 완료했습니다. Spring Boot 4.0.3 환경을 유지하면서 Redis, JPA, Mustache, H2를 유기적으로 사용하기 위한 의존성 주입과 환경 설정을 마쳤습니다.

## 변경 사항
1. **build.gradle**:
    - `spring-boot-starter-data-redis`: Redis 핵심 기능 사용.
    - `spring-boot-starter-data-jpa`: DB 접근 및 엔티티 관리.
    - `spring-boot-starter-mustache`: 뷰 렌더링.
    - `h2`: 인메모리 DB 사용.
    - `lombok`: 생산성 향상을 위한 어노테이션 도구.
2. **application.properties**:
    - H2 Console 활성화 (`/h2-console`) 및 인메모리 DB 연결 정보 설정.
    - JPA `ddl-auto: update` 설정을 통해 엔티티 변경 시 스키마 자동 동기화.
    - Redis 로컬 접속 설정 (`localhost:6379`).

## 검증 결과
- `./gradlew.bat clean build -x test` 실행 결과 `BUILD SUCCESSFUL`을 확인하여 의존성 충돌이나 설정 오류가 없음을 증명했습니다.

## 비유로 설명하는 이번 작업
이 작업은 마치 **"공부방을 꾸미는 것"**과 같습니다.
- 책장(JPA/DB)을 가져다 놓고,
- 참고서(Redis)를 준비하고,
- 게시판(Mustache)을 달아두고,
- 조명(H2 Console)을 켜서 공부할 준비를 완벽히 마친 상태입니다.
이제 본격적으로 Redis라는 책의 내용을 채워나갈 차례입니다!
