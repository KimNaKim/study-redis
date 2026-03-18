<!-- Parent: ../../../../../../../AI-CONTEXT.md -->

# src/test/java/com/example/redis_sampling/

## 목적
애플리케이션의 기능 및 동작을 검증하기 위한 테스트 코드를 관리합니다.

## 주요 파일
| 파일명 | 설명 |
|--------|------|
| RedisSamplingApplicationTests.java | 스프링 컨텍스트 로드 및 기본 동작 확인을 위한 테스트 클래스 |

## AI 작업 지침
- `common-rule.md`의 테스트 원칙에 따라 Redis와 H2 DB를 활용한 통합 테스트 위주로 작성할 것.
- TTL 동작 여부 등 Redis 특화 검증 로직 포함 권장.

## 테스트 실행
- `./gradlew test`를 통해 테스트 결과 확인 가능.

## 의존성
- 내부: Spring Boot Starter Test, Spring Data Redis
- 외부: JUnit 5, Mockito
