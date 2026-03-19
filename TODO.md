# TODO: Phase 1 - Redis 기초 & Strings 통합 (Caching) (완료)

이 단계의 목표는 JPA 데이터를 Redis에 캐싱하고 TTL을 설정하는 기본 인프라를 구축하는 것입니다. 특히, 개발 시작 전 Redis의 연결 상태를 시각적으로 확인할 수 있는 대시보드를 먼저 구축합니다.

## [x] 태스크 1: 프로젝트 의존성 및 환경 설정
- [x] `build.gradle`에 Redis, JPA, Mustache, H2 의존성 추가
- [x] `application.properties`에 DB 및 Redis 연결 설정 (H2 Console 활성화 포함)
- [x] **검증**: `gradle build` 성공 확인

## [x] 태스크 2: Redis 상태 모니터링 및 시각적 대시보드 구현 (Actuator 기반)
- [x] `build.gradle`에 `spring-boot-starter-actuator` 의존성 추가
- [x] `application.properties`에 Redis 헬스 체크 웹 노출 설정
- [x] `MonitoringController` 및 `index.mustache` 작성 (Redis 연결 상태 시각화: Green/Red 표시)
- [x] **검증**: 메인 화면(`/`)에서 Redis 연결 상태 "정상(UP)" 확인

## [x] 태스크 3: Redis 설정 및 직렬화 구현
- [x] `com.example.redis_sampling.config.RedisConfig` 작성
- [x] `RedisTemplate<String, Object>` 빈 등록
- [x] `StringRedisSerializer` (Key), `GenericJackson2JsonRedisSerializer` (Value) 설정
- [x] **검증**: RedisTemplate 빈 주입 여부 테스트

## [x] 태스크 4: JPA 도메인 및 DTO 구현 (Strings 기반)
- [x] `Product` JPA 엔티티 생성 (ID, 이름, 가격 등)
- [x] `ProductDto` 생성 (엔티티 주입 생성자 포함, 불변 객체)
- [x] `ProductRepository` 인터페이스 작성
- [x] **검증**: JPA를 통한 H2 데이터 저장 및 조회 테스트

## [x] 태스크 5: 캐싱 서비스 구현 및 TTL 적용
- [x] `ProductService` 구현 (조회 시 Redis 확인 후 없으면 DB 조회 및 캐싱)
- [x] 캐시 데이터 저장 시 60초 TTL 설정 적용
- [x] **검증**: `Strings` 타입으로 데이터가 JSON 형태로 Redis에 저장되는지 확인

## [x] 태스크 6: Web UI 통합 및 최종 리포트
- [x] `ProductController` 작성 (상품 목록 뷰)
- [x] `product-list.mustache` 작성 (캐싱 여부 표시 필드 포함)
- [x] 전체 흐름(DB -> Cache -> View) 검증 테스트 코드 작성
- [x] `.person/reports` 폴더에 작업 리포트 작성

---

# TODO: Phase 2 - Hashes (객체 필드 기반 관리)

이 단계의 목표는 Redis Hashes 데이터 구조를 활용하여 객체의 전체 데이터가 아닌 특정 필드(가격, 재고 등)만 효율적으로 관리하고 부분 업데이트를 구현하는 것입니다.

## [x] 태스크 1: 도메인 확장 및 초기 데이터 준비
- [x] `Product` 엔티티 및 `ProductDto`에 `stock` (재고) 필드 추가
- [x] `src/main/resources/data.sql`에 초기 재고 데이터 반영
- [x] **검증**: 애플리케이션 실행 시 H2 DB에 재고 정보가 정상적으로 로드되는지 확인

## [x] 태스크 2: Hash 전용 캐싱 서비스 구현
- [x] Step 2.1: `RedisTemplate` 설정 확인 및 `HashOperations` 활용 준비
- [x] Step 2.2: `ProductService`에 Hash 기반 저장 로직 구현 (`saveProductAsHash`)
- [x] Step 2.3: `ProductService`에 Hash 기반 조회 로직 구현 (`getProductFromHash`)
- [x] Step 2.4: 통합 테스트 코드 작성 및 검증 (Redis CLI 확인 포함)

## [ ] 태스크 3: 필드 단위 부분 업데이트 기능 구현
- [ ] `updateProductPriceInHash(Long id, Long newPrice)`: 가격 필드만 수정 (`HSET`)
- [ ] `decreaseStockInHash(Long id, Long quantity)`: `HINCRBY`를 이용한 원자적 재고 차감 구현
- [ ] **검증**: 가격 변경 시 Redis 내의 다른 필드(이름 등)는 유지되고 가격만 변하는지 확인

## [ ] 태스크 4: Strings vs Hashes 성능 및 구조 비교 테스트
- [ ] 테스트 코드 작성: 전체 객체 덮어쓰기(Strings) vs 부분 필드 수정(Hashes) 속도 비교
- [ ] 각 방식의 Redis 메모리 점유 구조 분석 및 정리
- [ ] **검증**: 다량의 업데이트 요청 시 Hashes 방식의 효율성 입증

## [ ] 태스크 5: Web UI 확장 및 실시간 반영 확인
- [ ] `product-list.mustache`에 '가격 수정' 및 '재고 차감' 버튼 추가
- [ ] `ProductController`에 부분 업데이트 요청을 처리하는 API 엔드포인트 구현
- [ ] UI에서 수정 후 새로고침 시 캐시 데이터가 즉시 반영되는지 확인
- [ ] **검증**: 최종 통합 테스트 및 Phase 2 작업 리포트 작성
