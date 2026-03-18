# Project Common Rules: redis-sampling

이 규칙은 `redis-sampling` 프로젝트의 코드 품질과 일관성을 유지하기 위해 반드시 준수해야 합니다.

## 1. 기술 스택 및 라이브러리
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle
- **Persistence**: Spring Data JPA & H2 (In-memory)
- **View Engine**: Mustache
- **Redis Client**: Spring Data Redis (`Lettuce` 기본 사용)
- **Serialization**: 
    - Key: `StringRedisSerializer`
    - Value: `GenericJackson2JsonRedisSerializer` (JSON 형태 저장)

## 2. Redis Key 네이밍 컨벤션
- 계층 구조를 나타내기 위해 `:`(콜론)을 구분자로 사용한다.
- 형식: `{service-name}:{domain}:{identifier}`
- 예시: `redis-lab:user:profile:123`

## 3. DTO 및 엔티티 설계 원칙
- **불변성 유지**: DTO는 `@Getter`만 사용하며, `Setter` 사용을 엄격히 금지한다.
- **엔티티 주입 생성자**: DTO는 해당 데이터를 담고 있는 JPA 엔티티를 파라미터로 받는 생성자를 반드시 가져야 한다.
    ```java
    public class UserDto {
        private final String id;
        private final String name;

        // JPA 엔티티를 직접 주입받는 생성자
        public UserDto(UserEntity entity) {
            this.id = entity.getId();
            this.name = entity.getName();
        }
    }
    ```
- **Lombok 활용**: `@Builder`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@AllArgsConstructor` 등을 적절히 활용한다.

## 4. 패키지 구조
- `com.example.redis_sampling.config`: Redis 연결 및 `RedisTemplate`, JPA 관련 설정.
- `com.example.redis_sampling.domain`: JPA 엔티티 및 Repository 인터페이스.
- `com.example.redis_sampling.dto`: Redis와 애플리케이션 간 데이터 전달 객체.
- `com.example.redis_sampling.service`: Redis 조작 및 비즈니스 로직.
- `com.example.redis_sampling.controller`: Mustache 뷰 컨트롤러 및 API 엔드포인트.

## 5. 테스트 및 검증
- 모든 기능 구현 시 `src/test`에 통합 테스트 코드를 작성한다.
- Redis 서버와 H2 DB가 구동 중인 환경에서 실제 데이터 흐름을 검증한다.
- TTL(Time To Live) 설정이 의도대로 동작하는지 확인하는 테스트 케이스를 포함한다.

