package com.dnd.jjigeojulge.reservation.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.reservation.application.dto.CreateReservationCommand;
import com.dnd.jjigeojulge.reservation.application.dto.UpdateReservationCommand;
import com.dnd.jjigeojulge.reservation.domain.Applicant;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.exception.ReservationNotFoundException;
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
                                                .toList());

                ScheduledTime scheduledTime = ScheduledTime.of(command.scheduledAt(), LocalDateTime.now());
                PlaceInfo placeInfo = PlaceInfo.of(
                                command.region1Depth(),
                                command.specificPlace(),
                                command.latitude(),
                                command.longitude());
                RequestMessage requestMessage = RequestMessage.from(command.requestMessage());

                Reservation reservation = Reservation.create(
                                ownerInfo,
                                scheduledTime,
                                placeInfo,
                                command.shootingDuration(),
                                requestMessage);

                return reservationRepository.save(reservation).getId();
        }

        public void updateReservation(UpdateReservationCommand command) {
                Reservation reservation = findReservationById(command.reservationId());

                LocalDateTime now = LocalDateTime.now();
                ScheduledTime scheduledTime = ScheduledTime.of(command.scheduledAt(), now);
                PlaceInfo placeInfo = PlaceInfo.of(
                                command.region1Depth(),
                                command.specificPlace(),
                                command.latitude(),
                                command.longitude());
                RequestMessage requestMessage = RequestMessage.from(command.requestMessage());

                reservation.update(
                                command.userId(),
                                scheduledTime,
                                placeInfo,
                                command.shootingDuration(),
                                requestMessage,
                                now);
        }

        public void applyToReservation(Long reservationId, Long userId) {
                Reservation reservation = findReservationById(reservationId);

                if (!userRepository.existsById(userId)) {
                        throw new UserNotFoundException();
                }

                Applicant applicant = Applicant.create(reservation, userId);
                reservation.apply(applicant, LocalDateTime.now());
                // TODO: 예약 작성자에게 지원 알림 발송 (Event Publisher 활용 권장)
        }

        public void acceptApplicant(Long reservationId, Long ownerId, Long applicantId) {
                Reservation reservation = findReservationById(reservationId);

                reservation.acceptApplicant(ownerId, applicantId, LocalDateTime.now());
                // TODO: 양쪽 사용자에게 매칭 확정 푸시 알림 발송 (명세서 2번)
        }

        public void cancelReservation(Long reservationId, Long userId) {
                Reservation reservation = findReservationById(reservationId);

                reservation.cancel(userId, LocalDateTime.now());
                // TODO: 매칭이 확정된 상태였다면 상대방에게 취소 알림 발송
        }

        public void completeReservation(Long reservationId) {
                Reservation reservation = findReservationById(reservationId);

                reservation.complete(LocalDateTime.now());
        }

        private Reservation findReservationById(Long reservationId) {
                return reservationRepository.findById(reservationId)
                                .orElseThrow(ReservationNotFoundException::new);
        }
}
