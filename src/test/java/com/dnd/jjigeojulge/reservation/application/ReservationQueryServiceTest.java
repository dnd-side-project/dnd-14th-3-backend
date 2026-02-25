package com.dnd.jjigeojulge.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.application.dto.query.CreatedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationListResponseDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationQueryRepository;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.user.domain.Gender;

@ExtendWith(MockitoExtension.class)
class ReservationQueryServiceTest {

        @InjectMocks
        private ReservationQueryService reservationQueryService;

        @Mock
        private ReservationQueryRepository reservationQueryRepository;

        @Test
        @DisplayName("예약 시간이 24시간 이내이고 상태가 RECRUITING이면 isImminent가 true로 매핑된다.")
        void searchReservations_isImminentTrue() {
                // given
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime scheduledAt = now.plusHours(10);

                ReservationSummaryDto dto = new ReservationSummaryDto(
                                1L, "테스트 제목", scheduledAt, Region1Depth.SEOUL, "장소", ShootingDurationOption.TEN_MINUTES,
                                List.of(), 50,
                                ReservationStatus.RECRUITING, 1L, "nick", Gender.MALE,
                                "url");
                Page<ReservationSummaryDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
                given(reservationQueryRepository.searchReservations(any(), any(),
                                org.mockito.ArgumentMatchers.anyInt())).willReturn(page);

                // when
                Page<ReservationListResponseDto> result = reservationQueryService
                                .searchReservations(ReservationSearchCondition.builder().build(),
                                                null, 10);

                // then
                assertThat(result.getContent().get(0).isImminent()).isTrue();
        }

        @Test
        @DisplayName("예약 시간이 24시간 이후면 isImminent가 false로 매핑된다.")
        void searchReservations_isImminentFalse_Over24Hours() {
                // given
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime scheduledAt = now.plusHours(25);

                ReservationSummaryDto dto = new ReservationSummaryDto(
                                1L, "테스트 제목", scheduledAt, Region1Depth.SEOUL, "장소", ShootingDurationOption.TEN_MINUTES,
                                List.of(), 50,
                                ReservationStatus.RECRUITING, 1L, "nick", Gender.MALE,
                                "url");
                Page<ReservationSummaryDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
                given(reservationQueryRepository.searchReservations(any(), any(),
                                org.mockito.ArgumentMatchers.anyInt())).willReturn(page);

                // when
                Page<ReservationListResponseDto> result = reservationQueryService
                                .searchReservations(ReservationSearchCondition.builder().build(),
                                                null, 10);

                // then
                assertThat(result.getContent().get(0).isImminent()).isFalse();
        }

        @Test
        @DisplayName("내가 올린 동행 예약 리스트를 조회한다.")
        void getMyCreatedReservations_Success() {
                // given
                Long ownerId = 1L;
                Long cursor = 10L;
                int limit = 5;

                CreatedReservationListDto dto = new CreatedReservationListDto(
                                1L, ReservationStatus.RECRUITING, "테스트", LocalDateTime.now().plusDays(1),
                                Region1Depth.SEOUL, "장소", ShootingDurationOption.TEN_MINUTES, 3L);
                Page<CreatedReservationListDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, limit), 1);

                given(reservationQueryRepository.getMyCreatedReservations(ownerId, cursor, limit)).willReturn(page);

                // when
                Page<CreatedReservationListDto> result = reservationQueryService.getMyCreatedReservations(ownerId,
                                cursor,
                                limit);

                // then
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).title()).isEqualTo("테스트");
        }
}
