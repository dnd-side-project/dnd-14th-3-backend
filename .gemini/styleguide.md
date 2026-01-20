# 우리 팀의 코드 리뷰 가이드라인

1. Spring Boot & Java 공통 규칙
   생성자 주입 권장: @Autowired 필드 주입 대신, final 필드와 Lombok의 @RequiredArgsConstructor를 이용한 생성자 주입을 사용합니다.

비즈니스 로직의 위치: 서비스 레이어는 트랜잭션 단위와 도메인 간의 흐름을 제어하며, 비즈니스 로직은 가능한 도메인 엔티티(Domain Entity) 내부에서 처리하도록 노력합니다. (풍부한 도메인 모델 지향)

DTO 사용 필수: 레이어 간 데이터 전송 시 엔티티를 직접 노출하지 않습니다. Request/Response 전용 DTO를 반드시 정의합니다.

2. ⚠️ 예외 처리 규칙 (가장 중요)
   Global Exception Handling: @RestControllerAdvice를 사용하여 프로젝트 전역에서 발생하는 예외를 한곳에서 관리합니다.

Custom Exception 정의: 비즈니스 의미가 담긴 RuntimeException 상속 예외를 만들어 사용합니다. (예: UserNotFoundException, InvalidCouponException)

에러 응답 규격: 에러 발생 시 단순히 문자열을 던지는 것이 아니라, 고유한 **에러 코드(ErrorCode)**와 메시지를 포함한 공통 에러 객체를 반환합니다.

Try-Catch 지양: 로직 내부에서 무분별한 try-catch로 예외를 삼키지 마세요. 예외는 발생 시점에서 명확히 던지거나, 전역 핸들러에서 처리합니다.

3. JPA 및 데이터베이스
   N+1 문제 경계: 연관 관계 조회 시 Fetch Join 또는 Batch Size 설정을 검토하여 불필요한 쿼리 발생을 방지합니다.

Dirty Checking 활용: 데이터 수정 시 save() 메서드를 명시적으로 호출하기보다, @Transactional 범위 내에서 영속성 컨텍스트의 변경 감지를 이용합니다.

불변 객체 권장: 조회 성능 최적화를 위해 @Transactional(readOnly = true)를 적절히 활용합니다.

4. 테스트 및 문서화
   단위 테스트 필수: 핵심 비즈니스 로직 및 예외 케이스(실패 케이스)에 대한 JUnit 5 테스트 코드를 반드시 포함합니다.

API 문서화: 새로운 API 추가 시 Swagger(SpringDoc) 또는 Spring Rest Docs 설정이 정확한지 확인합니다.
