<!-- Parent: ../../../../../../../AI-CONTEXT.md -->

# src/main/java/com/example/redis_sampling/

## 목적
Spring Boot 애플리케이션의 엔트리 포인트 및 핵심 소스 코드가 위치하는 디렉토리입니다.

## 주요 파일
| 파일명 | 설명 |
|--------|------|
| RedisSamplingApplication.java | Spring Boot 애플리케이션의 시작 클래스 |
| board/ | (현재는 비어 있거나 개발 예정인 공간) |

## 하위 디렉토리
- `controller/` - 애플리케이션의 뷰와 API를 담당하는 웹 계층
- `board/` - 게시판 관련 도메인 로직 (기초 패키지)

## AI 작업 지침
- 새로운 기능 추가 시 `common-rule.md`에 명시된 패키지 구조(`config`, `domain`, `dto`, `service`, `controller`)를 따를 것.
- Redis 관련 설정은 `config` 패키지에 위치시킬 것.

## 테스트
- `src/test/` 경로의 통합 테스트 코드를 통해 동작 검증.

## 의존성
- 내부: Spring Boot Starter Data Redis, Spring Boot Starter Web, Spring Boot Starter Data JPA
