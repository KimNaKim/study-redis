<!-- Parent: ../AI-CONTEXT.md -->

# src/main/resources/templates/

## 목적
Mustache 뷰 엔진을 사용하여 서버 데이터를 렌더링하는 HTML 템플릿 파일들이 위치합니다.

## 주요 파일
| 파일명 | 설명 |
|--------|------|
| index.mustache | Redis 연결 상태를 시각화하여 보여주는 메인 대시보드 템플릿 |

## 하위 디렉토리
(현재 추가적인 패키지 구조 없음)

## AI 작업 지침
- Bootstrap 등 외부 CSS 프레임워크는 CDN을 사용하여 최소한의 설정으로 UI 구성.
- 복잡한 로직은 컨트롤러나 서비스에서 처리하고, 템플릿에서는 데이터 출력 위주로 구성.

## 테스트
- 브라우저를 통한 수동 테스트 및 Selenium 등을 이용한 UI 테스트 가능.

## 의존성
- 내부: Spring Boot Starter Mustache, Bootstrap (CDN)
