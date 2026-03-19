# Redis Sampling 학습 로드맵

이 문서는 `redis-sampling` 프로젝트를 통해 학습할 Redis의 주요 기능과 단계별 목표를 기록합니다.

## Phase 1: Redis 기초 & Strings (Caching)
- [x] Redis 기본 설정 (RedisTemplate, Serializer 및 모니터링 대시보드 구축)
- [x] JPA & H2 DB 통합 (Persistence 계층 구성)
- [x] Strings 데이터 구조 활용 (단순 캐싱 구현)
- [x] TTL(Time To Live) 설정 및 만료 메커니즘 이해
- [x] Mustache를 이용한 캐싱 여부 시각화 (Web UI)

## Phase 2: Hashes (객체 필드 기반 관리)
- [x] Redis Hashes 데이터 구조 이해 및 `HashOperations` 활용
- [x] 객체 전체가 아닌 특정 필드(예: 상품 가격, 재고)만 업데이트하는 로직 구현
- [x] Strings 캐싱과 Hashes 캐싱의 성능 및 메모리 사용량 비교
- [x] **UI**: 특정 필드 수정 기능 추가 및 실시간 반영 확인

## Phase 3: Lists & Sets (컬렉션 활용)
- [ ] Lists를 활용한 '최근 조회한 상품' (Fixed-size Queue) 구현
- [ ] Sets를 활용한 '상품 좋아요' 및 '중복 없는 방문자 수' 집계
- [ ] `Lpush`, `Ltrim`, `Sadd`, `Scard` 등 주요 명령어 학습
- [ ] **UI**: 최근 본 상품 리스트 및 좋아요 버튼/카운트 표시

## Phase 4: Sorted Sets (실시간 리더보드)
- [ ] Sorted Sets(ZSet)를 이용한 '인기 상품 랭킹' 시스템 구축
- [ ] Score(조회수 또는 판매량) 기반의 실시간 정렬 및 순위 추출
- [ ] `Zadd`, `Zrevrange`, `Zrank` 등을 활용한 랭킹 조회 API 구현
- [ ] **UI**: 실시간 인기 상품 Top 10 대시보드 구현

## Phase 5: 고급 기능 (Pub/Sub & Geospatial)
- [ ] Redis Pub/Sub을 활용한 실시간 이벤트 알림 (예: 재고 부족 알림)
- [ ] Geospatial(Geo) 데이터를 활용한 '근처 물류 센터/매장 찾기' 기능
- [ ] Redis 트랜잭션 및 파이프라이닝(Pipelining) 성능 최적화
- [ ] **UI**: 실시간 알림 팝업 및 위치 기반 정보 시각화
