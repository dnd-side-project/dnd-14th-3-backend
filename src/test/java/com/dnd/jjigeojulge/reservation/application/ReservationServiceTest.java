package com.dnd.jjigeojulge.reservation.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
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
                        userId,
                        "서울특별시",
                        "강남역",
                        37.4979,
                        127.0276,
                        LocalDateTime.now().plusDays(1),
                        ShootingDurationOption.TWENTY_MINUTES,
                        "잘 부탁드려요"
                );

                // 유저 엔티티 준비 (Style: SNS_UPLOAD, FULL_BODY)
                User user = User.create(null, "nickname", Gender.MALE, "image", Set.of(
                        new PhotoStyle(StyleName.SNS_UPLOAD),
                        new PhotoStyle(StyleName.FULL_BODY)
                ));
                // 리플렉션으로 ID 주입 (BaseEntity id 필드)
                try {
                        java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class.getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(user, userId);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }

                given(userRepository.findByIdWithPhotoStyles(userId)).willReturn(Optional.of(user));
                given(reservationRepository.save(any(Reservation.class))).willAnswer(invocation -> invocation.getArgument(0));

                // when
                Long reservationId = reservationService.createReservation(command);

                // then
                verify(reservationRepository).save(argThat(reservation -> {
                        return reservation.getOwnerInfo().getPhotoStyleSnapshot().containsAll(List.of("SNS_UPLOAD", "FULL_BODY")) &&
                                reservation.getOwnerInfo().getUserId().equals(userId);
                }));
        }
}
