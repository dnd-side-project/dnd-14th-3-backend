### 📦 DDD + TDD 기반 예약 도메인 작업 단위 (Commit Plan)

#### **Commit 1: 핵심 도메인의 Enum 및 값 객체(Value Object) 개발**
- `ReservationStatus` Enum (모집 중, 확정됨, 모집 마감, 취소됨, 완료됨)
- `ApplicantStatus` Enum (지원, 선택됨, 미선택(거절), 취소)
- `ScheduledTime` (VO): 예약 시간 검증 (과거 시간 불가, 30분 단위 검증)
- `RequestMessage` (VO): 메시지 길이(최대 500자) 검증
- `PlaceInfo` (VO): 장소 정보 캡슐화 (내부에 `GeoPoint`, `specificPlace`)
- 단위 테스트 작성

#### **Commit 2: `Applicant` (지원자) 엔티티 개발**
- `Applicant` 엔티티 생성 (지원자 User ID, 지원 상태 매핑)
- 상태 변경 비즈니스 메서드 (`accept()`, `reject()`, `cancel()`)
- 단위 테스트 작성

#### **Commit 3: `Reservation` (예약) Aggregate Root 핵심 뼈대 개발**
- `Reservation` 엔티티 생성, VO(`ScheduledTime`, `PlaceInfo`, `RequestMessage`) 조립 및 초기 상태(`RECRUITING`) 세팅
- `PhotoStyleSnapshot` (생성 시점 사용자 프로필 촬영 유형 고정)
- 상태 검증을 포함한 수정(`update()`), 취소(`cancel()`) 메서드 구현
- 단위 테스트 작성

#### **Commit 4: [핵심 비즈니스] 지원자 관리 및 1:1 매칭 수락 로직 구현**
- `Reservation` 내 `List<Applicant> applicants` 연관관계 추가
- `apply(Applicant applicant)`: 중복 지원 방지
- `acceptApplicant(Long ownerId, Long applicantId)`: 1명 수락 시 나머지 자동 거절(`REJECTED`) 및 예약 확정(`CONFIRMED`)
- 단위 테스트 작성

#### **Commit 5: 영속성 계층 (JPA 연동 및 Repository)**
- 엔티티 어노테이션(`@Entity`, `@Embedded`, `@Enumerated`) 매핑
- `ReservationRepository`, `ApplicantRepository` 인터페이스 생성
- `@DataJpaTest` 단위 테스트 작성

#### **Commit 6: Application Layer (유스케이스 / Service 구현)**
- `ReservationService`, `ApplicantService` 구현 (생성, 수정, 취소, 지원, 확정)
- 매칭 확정 시 이벤트 발행(`ApplicationEventPublisher`)
- `@ExtendWith(MockitoExtension.class)` 서비스 단위 테스트

#### **Commit 7: 조회 모델 (목록 필터링 및 Read-only 쿼리)**
- 예약 탐색 리스트 필터링(JPA Specification/QueryDSL 등 활용)
- 지원자 리스트 조회 쿼리 구현
- 통합 테스트 작성

#### **Commit 8: 프레젠테이션 계층 연동 (Controller)**
- `ReservationController` API 서비스 연동
- `@WebMvcTest` 컨트롤러 슬라이스 테스트
