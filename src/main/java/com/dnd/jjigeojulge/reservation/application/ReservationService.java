package com.dnd.jjigeojulge.reservation.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.reservation.application.dto.CreateReservationCommand;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.domain.UserPhotoStyle;
import com.dnd.jjigeojulge.user.domain.exception.UserNotFoundException;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

        private final ReservationRepository reservationRepository;
        private final UserRepository userRepository;

        public Long createReservation(CreateReservationCommand command) {
                User user = userRepository.findByIdWithPhotoStyles(command.userId())
                        .orElseThrow(UserNotFoundException::new);

                OwnerInfo ownerInfo = OwnerInfo.of(
                        user.getId(),
                        user.getPhotoStyles().stream()
                                .map(UserPhotoStyle::getPhotoStyle)
                                .map(PhotoStyle::getName)
                                .map(Enum::name)
                                .toList()
                );

                ScheduledTime scheduledTime = ScheduledTime.of(command.scheduledAt(), LocalDateTime.now());
                PlaceInfo placeInfo = PlaceInfo.of(
                        command.region(),
                        command.specificPlace(),
                        command.latitude(),
                        command.longitude()
                );
                RequestMessage requestMessage = RequestMessage.from(command.requestMessage());

                Reservation reservation = Reservation.create(
                        ownerInfo,
                        scheduledTime,
                        placeInfo,
                        command.shootingDuration(),
                        requestMessage
                );

                return reservationRepository.save(reservation).getId();
        }
}
