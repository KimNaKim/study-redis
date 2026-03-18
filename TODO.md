# TODO: Phase 1 - Redis 기초 & Strings 통합 (Caching)

이 단계의 목표는 JPA 데이터를 Redis에 캐싱하고 TTL을 설정하는 기본 인프라를 구축하는 것입니다.

## [x] 태스크 1: 프로젝트 의존성 및 환경 설정
- [x] `build.gradle`에 Redis, JPA, Mustache, H2 의존성 추가
- [x] `application.properties`에 DB 및 Redis 연결 설정 (H2 Console 활성화 포함)
- [x] **검증**: `gradle build` 성공 확인

## [ ] 태스크 2: Redis 설정 및 직렬화 구현
- [ ] `com.example.redis_sampling.config.RedisConfig` 작성
- [ ] `RedisTemplate<String, Object>` 빈 등록
- [ ] `StringRedisSerializer` (Key), `GenericJackson2JsonRedisSerializer` (Value) 설정
- [ ] **검증**: RedisTemplate 빈 주입 여부 테스트

## [ ] 태스크 3: JPA 도메인 및 DTO 구현 (Strings 기반)
- [ ] `Product` JPA 엔티티 생성 (ID, 이름, 가격 등)
- [ ] `ProductDto` 생성 (엔티티 주입 생성자 포함, 불변 객체)
- [ ] `ProductRepository` 인터페이스 작성
- [ ] **검증**: JPA를 통한 H2 데이터 저장 및 조회 테스트

## [ ] 태스크 4: 캐싱 서비스 구현 및 TTL 적용
- [ ] `ProductService` 구현 (조회 시 Redis 확인 후 없으면 DB 조회 및 캐싱)
- [ ] 캐시 데이터 저장 시 60초 TTL 설정 적용
- [ ] **검증**: `Strings` 타입으로 데이터가 JSON 형태로 Redis에 저장되는지 확인

## [ ] 태스크 5: Web UI 통합 (Mustache)
- [ ] `ProductController` 작성 (뷰 컨트롤러)
- [ ] `product-list.mustache` 작성 (캐싱 여부 표시 필드 포함)
- [ ] **검증**: 브라우저를 통한 데이터 조회 및 캐시 적중(Cache Hit) 시각적 확인

## [ ] 태스크 6: 최종 통합 테스트 및 리포트
- [ ] 전체 흐름(DB -> Cache -> View) 검증 테스트 코드 작성
- [ ] `.person/reports` 폴더에 작업 리포트 작성
