<!-- Parent: ../../AI-CONTEXT.md -->

# src/main/resources/

## 목적
Spring Boot 애플리케이션의 설정 및 리소스(정적 파일, 템플릿)를 관리합니다.

## 주요 파일
| 파일명 | 설명 |
|--------|------|
| application.properties | 데이터베이스, Redis, 포트 등 주요 설정 정의 |

## 하위 디렉토리
- `static/` - 정적 리소스 (이미지, CSS, JS)
- `templates/` - Mustache 뷰 엔진용 템플릿 파일 (.mustache)

## 주요 파일
| 파일명 | 설명 |
|--------|------|
| application.properties | 데이터베이스, Redis, 포트 등 주요 설정 정의 |
| templates/index.mustache | 메인 상태 모니터링 대시보드 뷰 |

## AI 작업 지침
- Redis 포트 및 비밀번호 등은 환경 설정에서 동적으로 불러올 수 있게 처리.
- 뷰 개발 시 Mustache 문법을 활용하여 서버 데이터 렌더링.

## 테스트
- `application.properties`의 설정값이 테스트 환경에서 정상적으로 오버라이딩되는지 검증.

## 의존성
- 내부: Spring Boot Starter Mustache
