# Redis 상태 모니터링 대시보드 구현 리포트

**Reporter**: KimNaKim
**Date**: 2026-03-18
**Phase**: Phase 1 - Redis 기초 & Strings 통합 (Caching)

## 📝 작업 요약
Redis 개발을 본격적으로 시작하기 전, Redis 서버의 가동 상태를 직관적으로 확인할 수 있는 웹 기반 모니터링 대시보드를 구축했습니다.

## 🛠️ 변경 사항
- **의존성 추가**: `spring-boot-starter-actuator`를 도입하여 시스템 상태 정보를 API로 추출할 수 있게 함.
- **설정 최적화**: `application.properties`에서 Redis 헬스 체크 상세 내용을 외부로 노출하도록 구성.
- **모니터링 컨트롤러**: `HealthEndpoint`를 통해 Redis 엔진의 상태(UP/DOWN)를 실시간으로 판단하는 `MonitoringController` 구현.
- **프론트엔드 UI**: Mustache와 Bootstrap을 활용하여 녹색/빨간색 배지와 텍스트로 직관적인 상태를 보여주는 메인 페이지(`index.mustache`) 제작.

## ✅ 검증 결과
- **정상 케이스**: Redis 서버 구동 시 메인 화면에서 `UP` 상태와 녹색 배지가 정상적으로 표시됨.
- **예외 케이스**: Redis 서버 중단 시 메인 화면에서 `DOWN` 상태와 빨간색 경고 메시지가 즉시 노출됨을 확인.

## 💡 비유로 설명하기
> 이 작업은 마치 요리를 시작하기 전, 가스레인지의 불이 제대로 들어오는지 확인할 수 있는 **'상태 표시등'**을 설치한 것과 같습니다. 이제 재료(Data)를 손질하기 전에 언제든지 불(Redis)이 켜져 있는지 눈으로 바로 확인할 수 있어, 연결 문제로 당황하는 일을 방지할 수 있게 되었습니다!

---
**Status**: [x] 태스크 2 완료
