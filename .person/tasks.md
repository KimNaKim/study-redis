# Redis Sampling 학습 로드맵

이 문서는 `redis-sampling` 프로젝트를 통해 학습할 Redis의 주요 기능과 단계별 목표를 기록합니다.

## Phase 1: Redis 기초 & Strings (Caching)
- [ ] Redis 기본 설정 (RedisTemplate, Serializer 설정)
- [ ] JPA & H2 DB 통합 (Persistence 계층 구성)
- [ ] Strings 데이터 구조 활용 (단순 캐싱 구현)
- [ ] TTL(Time To Live) 설정 및 만료 메커니즘 이해
- [ ] Mustache를 이용한 캐싱 여부 시각화 (Web UI)

## Phase 2: 구조화된 데이터 & Hashes
- [ ] Hashes 데이터 구조 활용 (객체 단위 저장)
- [ ] 부분 업데이트(Partial Update) 기능 구현
- [ ] RedisRepository를 이용한 객체 매핑 학습

## Phase 3: 순서와 집합 (Lists & Sets)
- [ ] Lists 활용: 최근 본 상품 목록 (최근 N개 유지)
- [ ] Sets 활용: 중복 없는 방문자 수 체크 또는 '좋아요' 기능
- [ ] Redis의 집합 연산(Intersection, Union) 이해

## Phase 4: 실시간 랭킹 (Sorted Sets)
- [ ] Sorted Sets(ZSet) 활용: 점수 기반 리더보드 구현
- [ ] 실시간 데이터 변동에 따른 랭킹 자동 정렬 검증

## Phase 5: 고급 기능 (Pub/Sub & Distributed Lock)
- [ ] Redis Pub/Sub을 이용한 간단한 메시징 시스템
- [ ] Redisson을 활용한 분산 락(Distributed Lock)으로 동시성 문제 해결
- [ ] Lettuce vs Redisson 비교 분석

## Phase 6: 운영 및 최적화
- [ ] Redis 모니터링 및 성능 튜닝 기초
- [ ] Pipeline을 이용한 대량 데이터 처리 최적화
