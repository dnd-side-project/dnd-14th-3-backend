package com.dnd.jjigeojulge.reservation.infra;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.dnd.jjigeojulge.config.AppConfig;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.Applicant;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.vo.ReservationTitle;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;

@DataJpaTest
@Import({ReservationRepositoryImpl.class, AppConfig.class})
class ReservationRepositoryTest {

        @Autowired
        private ReservationRepository reservationRepository;

        @Test
        @DisplayName("Reservation 엔티티를 영속화하고 조회할 수 있다.")
        void saveAndFind() {
                // given
                OwnerInfo ownerInfo = OwnerInfo.of(1L, List.of("SNS_UPLOAD", "FULL_BODY"));
                LocalDateTime future = LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0);
                ScheduledTime scheduledTime = ScheduledTime.of(future, LocalDateTime.now());
                PlaceInfo placeInfo = PlaceInfo.of("서울특별시", "강남역", 37.4979, 127.0276);
                ShootingDurationOption shootingDuration = ShootingDurationOption.TWENTY_MINUTES;
                RequestMessage requestMessage = RequestMessage.from("사진 이쁘게 찍어주세요");

                Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime, placeInfo, shootingDuration, requestMessage);

                // when
                Reservation savedReservation = reservationRepository.save(reservation);
                Reservation foundReservation = reservationRepository.findById(savedReservation.getId()).orElseThrow();

                // then
                assertThat(foundReservation.getId()).isNotNull();
                assertThat(foundReservation.getOwnerInfo().getUserId()).isEqualTo(1L);
                assertThat(foundReservation.getStatus()).isEqualTo(ReservationStatus.RECRUITING);
                assertThat(foundReservation.getOwnerInfo().getPhotoStyleSnapshot()).containsExactly("SNS_UPLOAD", "FULL_BODY");
        }

        @Test
        @DisplayName("Reservation에 Applicant를 추가하여 영속화(Cascade) 할 수 있다.")
        void saveWithApplicants() {
                // given
                OwnerInfo ownerInfo = OwnerInfo.of(1L, List.of("SNS_UPLOAD"));
                LocalDateTime future = LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0);
                ScheduledTime scheduledTime = ScheduledTime.of(future, LocalDateTime.now());
                PlaceInfo placeInfo = PlaceInfo.of("서울특별시", "강남역", 37.4979, 127.0276);
                Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime, placeInfo, ShootingDurationOption.TEN_MINUTES, RequestMessage.from(""));

                Applicant applicant1 = Applicant.create(reservation, 2L);
                reservation.apply(applicant1, LocalDateTime.now());

                // when
                Reservation savedReservation = reservationRepository.save(reservation);
                Reservation foundReservation = reservationRepository.findById(savedReservation.getId()).orElseThrow();

                // then
                assertThat(foundReservation.getApplicants()).hasSize(1);
                assertThat(foundReservation.getApplicants().get(0).getUserId()).isEqualTo(2L);
        }
}
