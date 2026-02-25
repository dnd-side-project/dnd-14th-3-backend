package com.dnd.jjigeojulge.reservation.infra;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.global.config.TestJpaConfig;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ReservationTitle;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.OAuthInfo;
import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.domain.User;

import jakarta.persistence.EntityManager;

@DataJpaTest
@Import(TestJpaConfig.class)
class ReservationQueryRepositoryImplTest {

        @Autowired
        private EntityManager em;

        @Autowired
        private com.querydsl.jpa.impl.JPAQueryFactory queryFactory;

        private ReservationQueryRepositoryImpl queryRepository;

        @org.junit.jupiter.api.BeforeEach
        void setUp() {
                queryRepository = new ReservationQueryRepositoryImpl(queryFactory);
        }

        @Test
        @DisplayName("조건 없이 예약 목록을 조회하면 전체 예약 목록이 페이징되어 반환된다.")
        void searchReservations_NoConditions() {
                // given
                User user = User.create(new OAuthInfo("123", OAuthProvider.KAKAO), "testUser", Gender.MALE, "url",
                                null);
                em.persist(user);

                OwnerInfo ownerInfo1 = OwnerInfo.of(user.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name(),
                                                StyleName.PERSONAL_RECORD.name())));
                LocalDateTime startTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
                ScheduledTime scheduledTime = ScheduledTime.of(startTime, startTime.minusDays(1));
                PlaceInfo placeInfo = PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "서울숲", 37.5445, 127.0374);
                RequestMessage message = RequestMessage.from("사진 찍어주세요");

                Reservation reservation1 = Reservation.create(ownerInfo1, ReservationTitle.from("제목1"), scheduledTime,
                                placeInfo, ShootingDurationOption.THIRTY_PLUS_MINUTES,
                                RequestMessage.from("요청1"));
                em.persist(reservation1);

                OwnerInfo ownerInfo2 = OwnerInfo.of(user.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name(),
                                                StyleName.PERSONAL_RECORD.name())));
                Reservation reservation2 = Reservation.create(ownerInfo2, ReservationTitle.from("제목2"), scheduledTime,
                                placeInfo, ShootingDurationOption.THIRTY_PLUS_MINUTES,
                                RequestMessage.from("요청2"));
                em.persist(reservation2);

                em.flush();
                em.clear();

                ReservationSearchCondition condition = ReservationSearchCondition.builder().build();

                // when
                Page<ReservationSummaryDto> result = queryRepository.searchReservations(condition, null, 10);

                // then
                assertThat(result.getTotalElements()).isEqualTo(2);
                assertThat(result.getContent()).hasSize(2);
                assertThat(result.getContent().get(0).ownerNickname()).isEqualTo("testUser");
        }

        @Test
        @DisplayName("지역 필터로 예약 목록을 조회할 수 있다.")
        void searchReservations_WithRegionFilter() {
                // given
                User user = User.create(new OAuthInfo("123", OAuthProvider.KAKAO), "testUser", Gender.MALE, "url",
                                null);
                em.persist(user);

                OwnerInfo ownerInfo1 = OwnerInfo.of(user.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));
                LocalDateTime startTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
                ScheduledTime scheduledTime = ScheduledTime.of(startTime, startTime.minusDays(1));
                RequestMessage message = RequestMessage.from("사진 찍어주세요");

                Reservation seoulRes = Reservation.create(ownerInfo1, ReservationTitle.from("테스트 제목"), scheduledTime,
                                PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "서울숲", 37.5, 127.0),
                                ShootingDurationOption.TEN_MINUTES, message);
                em.persist(seoulRes);

                OwnerInfo ownerInfo2 = OwnerInfo.of(user.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));
                Reservation busanRes = Reservation.create(ownerInfo2, ReservationTitle.from("테스트 제목"), scheduledTime,
                                PlaceInfo.of(Region1Depth.BUSAN.getLabel(), "해운대", 35.1, 129.0),
                                ShootingDurationOption.TEN_MINUTES, message);
                em.persist(busanRes);

                em.flush();
                em.clear();

                ReservationSearchCondition condition = ReservationSearchCondition.builder()
                                .region1Depth(Region1Depth.SEOUL)
                                .date(null)
                                .photoStyle(null)
                                .gender(null)
                                .keyword(null)
                                .build();

                // when
                Page<ReservationSummaryDto> result = queryRepository.searchReservations(condition, null, 10);

                // then
                assertThat(result.getTotalElements()).isEqualTo(1);
                assertThat(result.getContent().get(0).specificPlace()).isEqualTo("서울숲");
        }

        @Test
        @DisplayName("키워드 필터(제목, 장소명)로 예약 목록을 조회할 수 있다.")
        void searchReservations_WithKeywordFilter() {
                // given
                User user = User.create(new OAuthInfo("123", OAuthProvider.KAKAO), "testUser", Gender.MALE, "url",
                                null);
                em.persist(user);

                OwnerInfo ownerInfo1 = OwnerInfo.of(user.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));
                LocalDateTime startTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
                ScheduledTime scheduledTime = ScheduledTime.of(startTime, startTime.minusDays(1));
                RequestMessage message = RequestMessage.from("사진 찍어주세요");

                Reservation seoulRes = Reservation.create(ownerInfo1, ReservationTitle.from("멋진 프로필"), scheduledTime,
                                PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "망원 한강공원", 37.5, 127.0),
                                ShootingDurationOption.TEN_MINUTES, message);
                em.persist(seoulRes);

                OwnerInfo ownerInfo2 = OwnerInfo.of(user.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));
                Reservation busanRes = Reservation.create(ownerInfo2, ReservationTitle.from("가족 사진"), scheduledTime,
                                PlaceInfo.of(Region1Depth.BUSAN.getLabel(), "해운대", 35.1, 129.0),
                                ShootingDurationOption.TEN_MINUTES, message);
                em.persist(busanRes);

                em.flush();
                em.clear();

                ReservationSearchCondition condition = ReservationSearchCondition.builder()
                                .keyword("망원")
                                .build();

                // when
                Page<ReservationSummaryDto> result = queryRepository.searchReservations(condition,
                                null, 10);

                // then
                assertThat(result.getTotalElements()).isEqualTo(1);
                assertThat(result.getContent().get(0).title()).isEqualTo("멋진 프로필");
        }

        @Test
        @DisplayName("성별 필터로 예약 목록을 조회할 수 있다.")
        void searchReservations_WithGenderFilter() {
                // given
                User maleUser = User.create(new OAuthInfo("123", OAuthProvider.KAKAO), "maleUser", Gender.MALE, "url",
                                null);
                em.persist(maleUser);
                User femaleUser = User.create(new OAuthInfo("456", OAuthProvider.KAKAO), "femaleUser", Gender.FEMALE,
                                "url",
                                null);
                em.persist(femaleUser);

                OwnerInfo maleOwner = OwnerInfo.of(maleUser.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));
                OwnerInfo femaleOwner = OwnerInfo.of(femaleUser.getId(),
                                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));

                LocalDateTime startTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
                ScheduledTime scheduledTime = ScheduledTime.of(startTime, startTime.minusDays(1));
                RequestMessage message = RequestMessage.from("사진 찍어주세요");

                Reservation maleRes = Reservation.create(maleOwner, ReservationTitle.from("남자 동행 구해"), scheduledTime,
                                PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "서울숲", 37.5, 127.0),
                                ShootingDurationOption.TEN_MINUTES, message);
                em.persist(maleRes);

                Reservation femaleRes = Reservation.create(femaleOwner, ReservationTitle.from("여자 동행 구해"),
                                scheduledTime,
                                PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "합정", 37.5, 127.0),
                                ShootingDurationOption.TEN_MINUTES, message);
                em.persist(femaleRes);

                em.flush();
                em.clear();

                ReservationSearchCondition condition = ReservationSearchCondition.builder()
                                .gender(Gender.FEMALE)
                                .build();

                // when
                Page<ReservationSummaryDto> result = queryRepository.searchReservations(condition,
                                null, 10);

                // then
                assertThat(result.getTotalElements()).isEqualTo(1);
                assertThat(result.getContent().get(0).title()).isEqualTo("여자 동행 구해");
        }

        @Test
        @DisplayName("사진 스타일 목록 조회 시 추가한 순서가 보장된다.")
        void searchReservations_WithStyleOrdering() {
                // given
                User user = User.create(new OAuthInfo("123", OAuthProvider.KAKAO), "testUser", Gender.MALE, "url",
                                null);
                em.persist(user);

                // 순서가 중요한 리스트 생성: SNS_UPLOAD, SYRUP_10, DAILY
                List<String> expectedStyles = List.of(StyleName.SNS_UPLOAD.name(), "SYRUP_10", "DAILY");
                OwnerInfo owner = OwnerInfo.of(user.getId(), expectedStyles);

                LocalDateTime startTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
                ScheduledTime scheduledTime = ScheduledTime.of(startTime, startTime.minusDays(1));
                RequestMessage message = RequestMessage.from("사진 찍어주세요");

                Reservation reservation = Reservation.create(owner, ReservationTitle.from("멋진 프로필"), scheduledTime,
                                PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "망원", 37.5, 127.0),
                                ShootingDurationOption.TEN_MINUTES, message);
                em.persist(reservation);

                em.flush();
                em.clear();

                ReservationSearchCondition condition = ReservationSearchCondition.builder().build();

                // when
                Page<ReservationSummaryDto> result = queryRepository.searchReservations(condition,
                                null, 10);

                // then
                assertThat(result.getTotalElements()).isEqualTo(1);
                List<String> actualStyles = result.getContent().get(0).photoStyleSnapshot();

                assertThat(actualStyles).hasSize(3);
                assertThat(actualStyles.get(0)).isEqualTo(StyleName.SNS_UPLOAD.name());
                assertThat(actualStyles.get(1)).isEqualTo("SYRUP_10");
                assertThat(actualStyles.get(2)).isEqualTo("DAILY");
        }
}
