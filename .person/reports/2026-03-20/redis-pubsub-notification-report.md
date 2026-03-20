# [Report] Redis Pub/Sub 실시간 재고 알림 시스템 구현
- **Date**: 2026-03-20
- **Reporter**: KimNaKim
- **Phase**: Phase 5 (Advanced Features - Pub/Sub)

## 📌 작업 요약
Redis의 **Pub/Sub(발행/구독)** 모델을 활용하여 상품의 재고가 일정 수준(5개) 미만으로 떨어졌을 때, 관리자나 사용자에게 실시간으로 알림을 전달하는 기능을 구현했습니다. 서버 내부의 이벤트 전파와 더불어 SSE(Server-Sent Events)를 연동하여 웹 브라우저까지 실시간 알림이 전달되도록 구성했습니다.

## 🛠️ 주요 변경 사항

### 1. Redis Pub/Sub 인프라 구축
- **RedisConfig**: `RedisMessageListenerContainer`와 `MessageListenerAdapter`를 설정하여 `inventory-alerts` 채널을 구독하도록 구성했습니다.
- **RedisPublisher**: `RedisTemplate.convertAndSend()`를 사용하여 메시지를 발행하는 공통 서비스를 구현했습니다.
- **RedisSubscriber**: Redis로부터 수신된 메시지를 받아 처리하는 리스너 클래스입니다.

### 2. 비즈니스 로직 연동 (Inventory Threshold)
- `ProductService.decreaseStock()` 메서드 수정:
    - 원자적 재고 차감(`HINCRBY`) 후 남은 재고가 5개 미만이면 알림 이벤트를 발행합니다.
    - `InventoryAlertDto`를 사용하여 상품 정보와 시간, 경고 메시지를 객체 형태로 전달합니다.

### 3. Web Real-time Notification (SSE)
- **SseController**: 클라이언트의 연결 세션을 `SseEmitter`로 관리하고, `broadcast()` 메서드를 통해 모든 연결된 클라이언트에게 실시간 알림을 푸시합니다.
- **Web UI (`product-list.mustache`)**:
    - `EventSource`를 사용하여 서버의 SSE 엔드포인트를 구독합니다.
    - 알림 수신 시 Bootstrap 기반의 Toast 스타일 알림 바를 동적으로 생성하여 표시합니다.

## 🧪 검증 결과
- **통합 테스트**: `InventoryAlertTest`를 통해 재고 차감 시 `ProductService` -> `RedisPublisher` -> `RedisSubscriber` -> `SseController`로 이어지는 파이프라인이 정상 동작함을 콘솔 로그로 확인했습니다.
- **UI 연동**: 페이지 로드 시 SSE 연결이 성공(INIT 메시지 수신)하고, 재고 부족 시 실시간으로 알림 팝업이 나타나는 구조를 완성했습니다.

## 💡 학습 포인트 및 비유
### Redis Pub/Sub은 "라디오 방송국"과 같습니다.
- **Publisher (방송국)**: 특정 주파수(채널)로 정보를 송출합니다. 누가 듣고 있는지 상관없이 일단 보냅니다.
- **Subscriber (라디오)**: 특정 주파수를 맞추고 있는 모든 라디오는 동시에 같은 방송을 듣게 됩니다.
- **SSE (확성기)**: 서버가 라디오 방송을 들은 뒤, 그 내용을 옆에 있는 사용자들에게 확성기(SSE)로 다시 전달해 주는 역할을 합니다.

이 구조를 통해 서비스 간의 결합도를 낮추면서(Loosely Coupled), 실시간성은 극대화할 수 있었습니다.

---
## 📊 진척도 현황
- **TODO.md**: Phase 5 태스크 1~4 완료 체크
- **.person/tasks.md**: Phase 5 (Pub/Sub & Optimization) 항목 완료
