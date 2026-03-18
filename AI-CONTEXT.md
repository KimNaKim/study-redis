# redis-sampling

## 목적
Redis를 활용한 데이터 샘플링 및 캐싱 전략을 실습하고 검증하는 Spring Boot 프로젝트입니다.

## 주요 파일
| 파일명 | 설명 |
|--------|------|
| build.gradle | 프로젝트 의존성 및 빌드 설정 (Spring Boot, Redis, JPA 등) |
| AI-GUIDE.md | AI 에이전트를 위한 세션 초기화 및 작업 가이드 |
| TODO.md | 실시간 작업 진척도 관리 파일 |

## 하위 디렉토리
- `src/main/java/com/example/redis_sampling/` - 비즈니스 로직 및 설정 코드
- `src/main/resources/` - 애플리케이션 설정 및 뷰 템플릿
- `src/test/` - 통합 및 단위 테스트
- `.ai/` - AI 에이전트용 스킬 및 규칙 정의
- `.person/` - 개인화된 작업 기록 및 보고서

## AI 작업 지침
- 모든 작업 전 `AI-GUIDE.md`의 초기화 루틴을 준수할 것.
- 코드 수정 시 `.ai/rules/common-rule.md`의 컨벤션을 따를 것.
- 작업 완료 후 `.person/reports/`에 보고서를 작성할 것.

## 테스트
- `./gradlew test` 명령어로 전체 테스트 실행 가능.

## 의존성
- 내부: Spring Data Redis (Lettuce), Spring Data JPA, Mustache
- 외부: Redis, H2 Database (In-memory)
