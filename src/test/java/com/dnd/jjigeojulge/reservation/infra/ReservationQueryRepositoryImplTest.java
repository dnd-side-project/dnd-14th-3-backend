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
        User user = User.create(new OAuthInfo("123", OAuthProvider.KAKAO), "testUser", Gender.MALE, "url", null);
        em.persist(user);

        OwnerInfo ownerInfo1 = OwnerInfo.of(user.getId(),
                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name(), StyleName.PERSONAL_RECORD.name())));
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
        ScheduledTime scheduledTime = ScheduledTime.of(startTime, startTime.minusDays(1));
        PlaceInfo placeInfo = PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "서울숲", 37.5445, 127.0374);
        RequestMessage message = RequestMessage.from("사진 찍어주세요");

        Reservation reservation1 = Reservation.create(ownerInfo1, ReservationTitle.from("제목1"), scheduledTime,
                placeInfo, ShootingDurationOption.THIRTY_PLUS_MINUTES,
                RequestMessage.from("요청1"));
        em.persist(reservation1);

        OwnerInfo ownerInfo2 = OwnerInfo.of(user.getId(),
                new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name(), StyleName.PERSONAL_RECORD.name())));
        Reservation reservation2 = Reservation.create(ownerInfo2, ReservationTitle.from("제목2"), scheduledTime,
                placeInfo, ShootingDurationOption.THIRTY_PLUS_MINUTES,
                RequestMessage.from("요청2"));
        em.persist(reservation2);

        em.flush();
        em.clear();

        ReservationSearchCondition condition = ReservationSearchCondition.builder().build();

        // when
        Page<ReservationSummaryDto> result = queryRepository.searchReservations(condition, PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).ownerNickname()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("지역 필터로 예약 목록을 조회할 수 있다.")
    void searchReservations_WithRegionFilter() {
        // given
        User user = User.create(new OAuthInfo("123", OAuthProvider.KAKAO), "testUser", Gender.MALE, "url", null);
        em.persist(user);

        OwnerInfo ownerInfo1 = OwnerInfo.of(user.getId(), new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
        ScheduledTime scheduledTime = ScheduledTime.of(startTime, startTime.minusDays(1));
        RequestMessage message = RequestMessage.from("사진 찍어주세요");

        Reservation seoulRes = Reservation.create(ownerInfo1, ReservationTitle.from("테스트 제목"), scheduledTime,
                PlaceInfo.of(Region1Depth.SEOUL.getLabel(), "서울숲", 37.5, 127.0),
                ShootingDurationOption.TEN_MINUTES, message);
        em.persist(seoulRes);

        OwnerInfo ownerInfo2 = OwnerInfo.of(user.getId(), new ArrayList<>(List.of(StyleName.SNS_UPLOAD.name())));
        Reservation busanRes = Reservation.create(ownerInfo2, ReservationTitle.from("테스트 제목"), scheduledTime,
                PlaceInfo.of(Region1Depth.BUSAN.getLabel(), "해운대", 35.1, 129.0),
                ShootingDurationOption.TEN_MINUTES, message);
        em.persist(busanRes);

        em.flush();
        em.clear();

        ReservationSearchCondition condition = ReservationSearchCondition.builder()
                .region1Depth(Region1Depth.SEOUL)
                .build();

        // when
        Page<ReservationSummaryDto> result = queryRepository.searchReservations(condition, PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).specificPlace()).isEqualTo("서울숲");
    }
}
