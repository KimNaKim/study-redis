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

## [x] 태스크 3: 필드 단위 부분 업데이트 기능 구현
- [x] Step 3.1: `ProductService`에 부분 업데이트 로직 구현 (`updateProductPrice`, `decreaseStock`)
- [x] Step 3.2: `ProductController`에 업데이트 API 엔드포인트 추가
- [x] Step 3.3: 비즈니스 로직 보호 (DB 동기화 고려) 및 예외 처리
- [x] Step 3.4: 통합 테스트 코드 작성 및 검증 (원자적 차감 확인 포함)

## [x] 태스크 4: Strings vs Hashes 성능 및 구조 비교 테스트
- [x] Step 4.1: 성능 벤치마크 테스트 로직 구현 (1,000회 반복 업데이트 비교)
- [x] Step 4.2: Redis 메모리 점유 및 구조 분석 (MEMORY USAGE 측정)
- [x] Step 4.3: 테스트 결과 시각화 및 최종 리포트 작성 (UI 로그 출력 포함)

---

# TODO: Phase 3 - Lists & Sets (컬렉션 활용)

이 단계의 목표는 Redis의 컬렉션 타입인 Lists와 Sets를 활용하여 선입선출(FIFO) 기반의 활동 로그(최근 본 상품)와 중복을 허용하지 않는 데이터(좋아요, 방문자 집계)를 구현하는 것입니다.

## [ ] 태스크 1: Lists를 활용한 '최근 조회한 상품' 기능 구현
- [x] Step 1.1: `ProductService`에 최근 본 상품 추가 로직 구현 (`Lpush` & `Ltrim`으로 최대 5개 유지)
- [x] Step 1.2: `ProductService`에 최근 본 상품 목록 조회 로직 구현 (`Lrange`)
- [x] Step 1.3: 상품 상세 조회(또는 목록 클릭) 시 자동으로 리스트에 추가되도록 연동
- [ ] **검증**: 동일 상품 중복 노출 방지 로직 및 고정 크기(5개) 유지 테스트

## [x] 태스크 2: Sets를 활용한 '상품 좋아요' 기능 구현
- [x] Step 2.1: `ProductService`에 좋아요 추가/취소 로직 구현 (`Sadd`, `Srem`)
- [x] Step 2.2: 특정 사용자의 좋아요 여부 확인 (`Sismember`) 및 총 좋아요 수 조회 (`Scard`)
- [x] Step 2.3: `ProductDto`에 좋아요 상태 및 카운트 필드 추가
- [x] **검증**: 동일 사용자의 중복 좋아요 방지 및 실시간 카운트 증감 확인

## [x] 태스크 3: Sets를 활용한 '오늘의 유니크 방문자(UV)' 집계
- [x] Step 3.1: 접속 시 사용자 식별자(IP 또는 임시 ID)를 Redis Set에 저장 (일일 단위 키 설계)
- [x] Step 3.2: 전체 방문자 수 조회 API 및 대시보드 연동
- [x] **검증**: 동일 사용자의 반복 접속에도 방문자 수(UV)가 유지되는지 확인

## [x] 태스크 4: Web UI 확장 및 컬렉션 데이터 시각화
- [x] Step 4.1: `index.mustache` 또는 `product-list.mustache`에 '최근 본 상품' 사이드바 추가
- [x] Step 4.2: 각 상품 항목에 '좋아요' 버튼 및 카운트 표시 (AJAX 통신 고려)
- [x] Step 4.3: 메인 대시보드에 '오늘의 방문자 수' 수치 표시
- [x] **검증**: 클릭 이벤트 발생 시 UI에 실시간 데이터 반영 확인

---

# TODO: Phase 4 - Sorted Sets (실시간 리더보드)

이 단계의 목표는 Redis의 Sorted Sets(ZSet) 자료구조를 활용하여 Score(조회수) 기반의 실시간 상품 랭킹 시스템을 구축하는 것입니다.

## [x] 태스크 1: 가중치(Score) 기반 상품 조회수 집계
- [x] Step 1.1: `ProductService`에 상품 조회수 증가 로직 구현 (`Zincrby`)
- [x] Step 1.2: 상품 상세 조회(`getProductFromHash`) 호출 시 조회수 증가 연동
- [x] **검증**: Redis CLI에서 `ZSCORE` 명령으로 특정 상품의 점수가 정상 증가하는지 확인

## [x] 태스크 3: 실시간 인기 랭킹 조회 기능 구현
- [x] Step 2.1: `ProductService`에 실시간 Top 5 랭킹 조회 로직 구현 (`ZrevrangeWithScores`)
- [x] Step 2.2: `ProductController`에 랭킹 조회 API 엔드포인트 추가
- [x] **검증**: 점수가 높은 순서대로 리스트가 반환되는지 확인

## [x] 태스크 3: Web UI 확장 및 실시간 리더보드 시각화
- [x] Step 3.1: `product-list.mustache`에 '실시간 인기 상품 Top 5' 섹션 추가
- [x] Step 3.2: 일정 시간마다 또는 특정 이벤트 시 랭킹 목록을 갱신하는 JS 로직 추가
- [x] **검증**: 여러 상품을 클릭했을 때 실시간으로 순위가 변동되는지 확인

## [x] 태스크 4: Phase 4 최종 검증 및 리포트 작성
- [x] Step 4.1: Sorted Sets 통합 테스트 코드 작성 (사용자 요청으로 생략)
- [x] Step 4.2: `.person/reports` 폴더에 작업 리포트 작성 및 Phase 4 완료 보고
