<!-- Parent: ../AI-CONTEXT.md -->

# src/main/java/com/example/redis_sampling/controller/

## 목적
Spring MVC 컨트롤러를 통해 요청을 처리하고 데이터를 뷰(Mustache)에 전달하거나 API 응답을 생성합니다.

## 주요 파일
| 파일명 | 설명 |
|--------|------|
| MonitoringController.java | 인프라(Redis 등) 상태를 확인하고 메인 대시보드 화면을 렌더링함. |

## 하위 디렉토리
(현재 추가적인 패키지 구조 없음)

## AI 작업 지침
- 비즈니스 로직은 `service` 계층에 위임하고, 컨트롤러는 데이터 매핑 및 화면 전환에 집중할 것.
- 뷰 이름 반환 시 접두사/접미사는 `application.properties` 설정을 따를 것.

## 테스트
- `MockMvc`를 활용한 컨트롤러 테스트 코드 작성을 권장.

## 의존성
- 내부: Spring Actuator (HealthEndpoint), Spring MVC
