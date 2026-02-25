package com.dnd.jjigeojulge.reservation.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.application.dto.CreateReservationCommand;
import com.dnd.jjigeojulge.reservation.application.dto.UpdateReservationCommand;
import com.dnd.jjigeojulge.reservation.domain.Applicant;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.infra.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

        @Mock
        private ReservationRepository reservationRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private ReservationService reservationService;

        @Test
        @DisplayName("예약을 생성할 때 유저의 현재 촬영 스타일을 스냅샷으로 저장한다.")
        void createReservation_WithPhotoStyleSnapshot() {
                // given
                Long userId = 1L;
                CreateReservationCommand command = new CreateReservationCommand(
                                userId, "멋진 제주도 스냅", "서울특별시",
                                "강남역",
                                37.4979,
                                127.0276,
                                LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0),
                                ShootingDurationOption.TWENTY_MINUTES,
                                "잘 부탁드려요");

                User user = User.create(null, "nickname", Gender.MALE, "image", Set.of(
                                new PhotoStyle(StyleName.SNS_UPLOAD),
                                new PhotoStyle(StyleName.FULL_BODY)));
                setId(user, userId);

                given(userRepository.findByIdWithPhotoStyles(userId)).willReturn(Optional.of(user));
                given(reservationRepository.save(any(Reservation.class)))
                                .willAnswer(invocation -> invocation.getArgument(0));

                // when
                Long reservationId = reservationService.createReservation(command);

                // then
                verify(reservationRepository).save(argThat(reservation -> {
                        return reservation.getOwnerInfo().getPhotoStyleSnapshot()
                                        .containsAll(List.of("SNS_UPLOAD", "FULL_BODY")) &&
                                        reservation.getOwnerInfo().getUserId().equals(userId);
                }));
        }

        @Test
        @DisplayName("모집 중인 예약 정보를 수정한다.")
        void updateReservation_Success() {
                // given
                Long reservationId = 1L;
                Long ownerId = 10L;
                UpdateReservationCommand command = new UpdateReservationCommand(
                                reservationId, ownerId, "여행 사진 부탁드려요", "서울특별시", "홍대입구역", 37.5568, 126.9242,
                                LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0),
                                ShootingDurationOption.THIRTY_PLUS_MINUTES, "메시지 수정");
                Reservation reservation = mock(Reservation.class);
                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

                // when
                reservationService.updateReservation(command);

                // then
                verify(reservation).update(eq(ownerId), any(), any(), any(),
                                eq(ShootingDurationOption.THIRTY_PLUS_MINUTES),
                                any(), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("모집 중인 예약에 지원할 수 있다.")
        void applyToReservation_Success() {
                // given
                Long reservationId = 1L;
                Long applicantId = 2L;
                Reservation reservation = mock(Reservation.class);

                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
                given(userRepository.existsById(applicantId)).willReturn(true);

                // when
                reservationService.applyToReservation(reservationId, applicantId);

                // then
                verify(reservation).apply(any(Applicant.class), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("예약 작성자가 지원자를 수락하여 매칭을 확정한다.")
        void acceptApplicant_Success() {
                // given
                Long reservationId = 1L;
                Long ownerId = 10L;
                Long applicantId = 20L;
                Reservation reservation = mock(Reservation.class);

                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

                // when
                reservationService.acceptApplicant(reservationId, ownerId, applicantId);

                // then
                verify(reservation).acceptApplicant(eq(ownerId), eq(applicantId), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("예약 작성자가 예약을 취소한다.")
        void cancelReservation_Success() {
                // given
                Long reservationId = 1L;
                Long userId = 10L;
                Reservation reservation = mock(Reservation.class);

                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

                // when
                reservationService.cancelReservation(reservationId, userId);

                // then
                verify(reservation).cancel(eq(userId), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("완료(COMPLETED) 처리는 Service를 통해 이루어질 수 있다.")
        void completeReservation() {
                // given
                Long reservationId = 1L;
                Reservation reservation = mock(Reservation.class);
                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

                // when
                reservationService.completeReservation(reservationId);

                // then
                verify(reservation).complete(any(LocalDateTime.class));
        }

        @Test
        @DisplayName("지원자는 예약에 대한 지원을 취소할 수 있다.")
        void cancelApplicationToReservation_Success() {
                // given
                Long reservationId = 1L;
                Long applicantId = 2L;
                Reservation reservation = mock(Reservation.class);

                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

                // when
                reservationService.cancelApplicationToReservation(reservationId, applicantId);

                // then
                verify(reservation).cancelApplication(eq(applicantId), any(LocalDateTime.class));
        }

        private void setId(Object entity, Long id) {
                try {
                        java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class
                                        .getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(entity, id);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
}
